using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using System.Threading.RateLimiting;
using Yarp.ReverseProxy.Transforms;

var builder = WebApplication.CreateBuilder(args);

// --- 1. LOGGING CONFIG ---
// Thiết lập để Console log ra đẹp và chi tiết hơn
builder.Logging.ClearProviders();
builder.Logging.AddConsole();

// --- 2. SERVICES CONFIG ---
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowFrontend", policy =>
    {
        policy.WithOrigins("http://localhost:3000", "http://localhost:5173")
              .AllowAnyHeader().AllowAnyMethod().AllowCredentials();
    });
});
var jwtSettings = builder.Configuration.GetSection("JwtSettings");
var secretKey = jwtSettings["SecretKey"];
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            // ValidIssuer = jwtSettings["Issuer"],
            // ValidAudience = jwtSettings["Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey))
        };
    });

builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("RequireLoggedIn", policy => policy.RequireAuthenticatedUser());
});

builder.Services.AddRateLimiter(options =>
{
    options.AddFixedWindowLimiter("StrictPolicy", opt =>
    {
        opt.PermitLimit = 5;
        opt.Window = TimeSpan.FromSeconds(10);
        opt.QueueLimit = 0;
    });
});

builder.Services.AddReverseProxy()
    .LoadFromConfig(builder.Configuration.GetSection("ReverseProxy"))
    .AddTransforms(builderContext =>
    {
        // Transform này sẽ chạy cho TẤT CẢ các route có check Auth
        builderContext.AddRequestTransform(async transformContext =>
        {
            var user = transformContext.HttpContext.User;
            var logger = transformContext.HttpContext.RequestServices.GetRequiredService<ILogger<Program>>();

            if (user.Identity?.IsAuthenticated == true)
            {
                // 1. Lấy thông tin từ Claim (JWT bóc ra)
                var userId = user.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value
                             ?? user.FindFirst("sub")?.Value;
                var email = user.FindFirst(System.Security.Claims.ClaimTypes.Email)?.Value
                             ?? user.FindFirst("email")?.Value;

                // 2. Gắn vào Header trước khi gửi xuống Java/NestJS
                if (!string.IsNullOrEmpty(userId))
                {
                    transformContext.ProxyRequest.Headers.Add("x-user-id", userId);
                }
                if (!string.IsNullOrEmpty(email))
                {
                    transformContext.ProxyRequest.Headers.Add("x-user-email", email);
                }

                // 3. LOG ĐỂ DEBUG - Nhìn phát biết ngay Gateway có đang làm việc không
                logger.LogInformation("--- [GATEWAY SECURITY TRANSFORM] ---");
                logger.LogInformation($"🔑 Authenticated User: {email}");
                logger.LogInformation($"🆔 Injecting Header x-user-id: {userId}");
            }
        });
    });

var app = builder.Build();
// --- LOG 1: CHỈ LOG INCOMING (KHÔNG ĐƯỢC GỌI GETREVERSEPROXYFEATURE Ở ĐÂY) ---
app.Use(async (context, next) =>
{
    var logger = app.Services.GetRequiredService<ILogger<Program>>();
    logger.LogInformation($"📥 [RECEIVE] {context.Request.Method} {context.Request.Path}");

    await next(); // Cho request đi tiếp xuống các middleware khác
});
// --- 3. LOG KHI STARTUP ---
var logger = app.Services.GetRequiredService<ILogger<Program>>();
var lifetime = app.Services.GetRequiredService<IHostApplicationLifetime>();

lifetime.ApplicationStarted.Register(() =>
{
    var urls = app.Urls; // Lấy danh sách các cổng đang chạy
    Console.ForegroundColor = ConsoleColor.Cyan;
    Console.WriteLine("\n" + new string('=', 50));
    logger.LogInformation("🚀 SLACK CLONE API GATEWAY IS RUNNING");
    foreach (var url in urls)
    {
        logger.LogInformation($"📡 Listening on: {url}");
    }
    logger.LogInformation("🛡️  Auth Policy: RequireLoggedIn [ENABLED]");
    logger.LogInformation("⏳ Rate Limit Policy: StrictPolicy [ENABLED]");
    Console.WriteLine(new string('=', 50) + "\n");
    Console.ResetColor();
});



// --- 4. MIDDLEWARES ---
app.UseCors("AllowFrontend");
app.UseAuthentication();
app.UseAuthorization();
app.UseRateLimiter();

// --- LOG 2: LOG FORWARDING (CHỈ CHẠY KHI YARP ĐÃ MATCH ĐƯỢC ROUTE) ---
app.MapReverseProxy(proxyPipeline =>
{
    // Middleware này chỉ chạy bên trong "đường ống" của YARP
    proxyPipeline.Use(async (context, next) =>
    {
        var logger = context.RequestServices.GetRequiredService<ILogger<Program>>();

        // Gọi TRƯỚC khi Forward để biết nó định đi đâu
        var proxyFeature = context.GetReverseProxyFeature();
        var destination = proxyFeature.ProxiedDestination?.Model.Config.Address;
        logger.LogInformation($"➡️ [MATCHED] Route: {proxyFeature.Route.Config.RouteId} -> Forwarding to: {destination}");

        await next(); // Thực hiện Forward thực tế

        // Gọi SAU khi Forward để lấy Status Code
        logger.LogInformation($"✅ [COMPLETED] Status: {context.Response.StatusCode}");
    });
});
app.Run();


