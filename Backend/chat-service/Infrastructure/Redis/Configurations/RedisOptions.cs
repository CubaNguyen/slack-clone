namespace ChatService.Infrastructure.Redis.Configurations;

public class RedisOptions
{
    // Tên của section trong file appsettings.json
    public const string Redis = "Redis";

    // Địa chỉ Redis (localhost:6379)
    public string Configuration { get; set; } = string.Empty;

    // Tiền tố (ChatService_)
    public string InstanceName { get; set; } = string.Empty;
}