using Microsoft.EntityFrameworkCore;
using ChatService.Infrastructure.Persistence;
using Infrastructure.Persistence;
using Application.Common.Interfaces;
using ChatService.Infrastructure.Middleware;
using System.Reflection; // 👈 Cần thêm cái này để quét Assembly
using StackExchange.Redis;
using ChatService.Infrastructure.Redis.Configurations;
using ChatService.Infrastructure.Redis.Services;
using Microsoft.OpenApi;
using Infrastructure.Services;
using MediatR;
using ChatService.Application.Common.dto.Interfaces;
using ChatService.Infrastructure.Kafka.Services;
using ChatService.Application.Common.Interfaces;
using ChatService.Infrastructure.Kafka.Consumers;
var builder = WebApplication.CreateBuilder(args);

// --- 1. ĐĂNG KÝ SERVICES (Chuẩn bị nguyên liệu) ---

// 1.1. Controller & API
builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.Converters.Add(new System.Text.Json.Serialization.JsonStringEnumConverter());
        // Đảm bảo nó không can thiệp thô bạo vào DateTime
        options.JsonSerializerOptions.PropertyNamingPolicy = System.Text.Json.JsonNamingPolicy.CamelCase;
        options.JsonSerializerOptions.Converters.Add(new System.Text.Json.Serialization.JsonStringEnumConverter());
    });
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Slack Clone - Chat Service API",
        Version = "v1",
        Description = "API xử lý tin nhắn real-time, kết nối Redis và Kafka.",
        Contact = new OpenApiContact
        {
            Name = "Bộ phận kỹ thuật",
            Email = "support@yourproject.com"
        }
    });

    // Quan trọng: Để Swagger đọc được các file XML Comment ở Bước 1
    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    c.IncludeXmlComments(xmlPath);
});
builder.Services.AddAuthorization(); // Sửa lỗi sập app lúc đầu

// 1.2. Database
builder.Services.AddScoped<ISqlConnectionFactory, SqlConnectionFactory>();
builder.Services.AddScoped<IApplicationDbContext>(provider =>
    provider.GetRequiredService<ChatDbContext>());
builder.Services.AddDbContext<ChatDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// 1.3. MediatR (👇 QUAN TRỌNG: Sửa lỗi "No service for type ISender")
// Đoạn này giúp code tìm thấy các file xử lý logic (Command/Query)
builder.Services.AddMediatR(cfg =>
{
    // Cách an toàn nhất cho người mới: Quét tất cả các thư viện đang chạy để tìm Handler
    cfg.RegisterServicesFromAssemblies(AppDomain.CurrentDomain.GetAssemblies());

    cfg.AddBehavior(typeof(IPipelineBehavior<,>), typeof(ChannelAuthorizationBehavior<,>));
});

// 1.4. Background Services (Các tác vụ chạy ngầm)
// - Consumer: Để NHẬN tin từ Kafka
// builder.Services.AddHostedService<ChatService.Infrastructure.Kafka.Consumers.WorkspaceEventsConsumer>();
builder.Services.AddSingleton<IKafkaProducer, KafkaProducer>();
builder.Services.AddTransient<IKafkaConsumer, KafkaConsumer>();

// - Worker: Để GỬI tin đi Kafka (Outbox Pattern) - 👇 Đừng quên dòng này nếu muốn gửi tin!
// (Bỏ comment dòng dưới nếu bạn đã tạo file OutboxWorker như mình hướng dẫn)
// builder.Services.AddHostedService<ChatService.Infrastructure.Outbox.Worker.OutboxWorker>(); 
builder.Services.AddHostedService<ChannelReadBatchConsumer>();
builder.Services.AddHostedService<ThreadMetadataConsumer>();
// --- 1.5. Redis (Phát sóng Real-time) ---


// A. Đọc config từ appsettings.json
builder.Services.Configure<RedisOptions>(builder.Configuration.GetSection("Redis"));

// B. Đăng ký kết nối Redis (Singleton - Một kết nối dùng mãi mãi)
// B. Đăng ký kết nối Redis (Singleton)
builder.Services.AddSingleton<IConnectionMultiplexer>(sp =>
{
    var configuration = builder.Configuration.GetSection("Redis:Configuration").Value ?? "localhost:6379";

    try
    {
        var connection = ConnectionMultiplexer.Connect(configuration);

        // In ra console để nhận diện ngay lập tức
        Console.ForegroundColor = ConsoleColor.Cyan;
        Console.WriteLine($"[REDIS INFO] Đã kết nối thành công tới: {configuration}");
        Console.ResetColor();

        return connection;
    }
    catch (Exception ex)
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine($"[REDIS ERROR] Không thể kết nối Redis: {ex.Message}");
        Console.ResetColor();
        throw; // Ném lỗi để dừng app nếu Redis là bắt buộc
    }
});

// C. Đăng ký Service của mình để các nơi khác gọi dùng
builder.Services.AddSingleton<IRedisBusService, RedisBusService>();
builder.Services.AddSingleton<IThreadCacheService, ThreadCacheService>();
builder.Services.AddSingleton<ICacheService, RedisCacheService>();
// --- 1.6. User Context (Định danh người dùng từ Gateway) ---
// Đăng ký HttpContextAccessor để Service có thể thọc tay vào Header của Request
builder.Services.AddHttpContextAccessor();

// Đăng ký Service thực thi UserContext (Nằm ở Infrastructure)
// Đổi 'UserContext' thành tên class thực tế của bạn nếu khác
builder.Services.AddScoped<ICurentUserContext, UserContext>();
// --- 2. BUILD APP ---
var app = builder.Build();

// --- 3. CẤU HÌNH PIPELINE (Luồng xử lý Request) ---

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseMiddleware<GlobalExceptionMiddleware>();

// app.UseHttpsRedirection(); // Tạm tắt nếu bạn chỉ test http local cho nhẹ
app.UseAuthorization(); // Đã có AddAuthorization ở trên nên dòng này sẽ chạy ngon

app.MapControllers();

// --- 4. LOGGING & RUN ---
var logger = app.Services.GetRequiredService<ILogger<Program>>();

// Dòng log này sẽ hiện ra ở Console khi bạn bấm chạy app
logger.LogInformation("================================================");
logger.LogInformation("🚀 Chat Service is running on: http://localhost:3003");
logger.LogInformation("📖 Swagger UI: http://localhost:3003/swagger/index.html");
logger.LogInformation("================================================");

app.Run();