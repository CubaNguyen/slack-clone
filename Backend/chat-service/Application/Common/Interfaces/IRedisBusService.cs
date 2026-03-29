namespace ChatService.Infrastructure.Redis.Services;

public interface IRedisBusService
{
    // Bắn tin nhắn lên một channel của Redis
    Task PublishAsync(string channel, string message);

    // Đăng ký lắng nghe một channel (Dùng cho Realtime Service)
    Task SubscribeAsync(string channel, Action<string> handler);

}