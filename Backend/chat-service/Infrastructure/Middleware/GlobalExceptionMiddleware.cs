using System.Net;
using System.Text.Json;
using ChatService.Application.Common.Models;
using ChatService.Domain.Common;
using ChatService.Domain.Exceptions;
using Microsoft.AspNetCore.Http;

namespace ChatService.Infrastructure.Middleware;

public class GlobalExceptionMiddleware
{
    private readonly RequestDelegate _next;

    public GlobalExceptionMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task Invoke(HttpContext context)
    {
        try
        {
            await _next(context); // Cho request đi qua
        }
        catch (Exception ex)
        {
            await HandleExceptionAsync(context, ex); // Nếu có lỗi thì xử lý
        }
    }

    private static Task HandleExceptionAsync(HttpContext context, Exception exception)
    {
        context.Response.ContentType = "application/json";

        // Mặc định là lỗi 500
        var errorCode = ErrorCode.Uncategorized;
        var statusCode = HttpStatusCode.InternalServerError;
        var message = exception.Message;

        // Nếu là lỗi mình tự định nghĩa (AppException)
        if (exception is AppException appException)
        {
            errorCode = appException.ErrorCode;
            statusCode = appException.ErrorCode.StatusCode;
            message = appException.ErrorCode.Message;
        }

        context.Response.StatusCode = (int)statusCode;

        // Tạo cục JSON trả về
        var response = ApiResponse<object>.Fail(message, new ErrorDetail
        {
            Code = errorCode.Code,
            Path = context.Request.Path,
            Timestamp = DateTime.UtcNow
        });

        // Serialize ra JSON (viết hoa chữ cái đầu hay camelCase tùy config, ở đây mặc định camelCase)
        var jsonOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
        var json = JsonSerializer.Serialize(response, jsonOptions);

        return context.Response.WriteAsync(json);
    }
}