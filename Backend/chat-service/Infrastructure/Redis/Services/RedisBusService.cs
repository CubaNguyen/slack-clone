using StackExchange.Redis;
using ChatService.Infrastructure.Redis.Configurations;
using Microsoft.Extensions.Options;

namespace ChatService.Infrastructure.Redis.Services;

public class RedisBusService : IRedisBusService
{
    private readonly IConnectionMultiplexer _redis;
    private readonly string _prefix;

    public RedisBusService(IConnectionMultiplexer redis, IOptions<RedisOptions> options)
    {
        _redis = redis;
        _prefix = options.Value.InstanceName; // Đây là chỗ dùng "ChatService_" nè
    }

    public async Task PublishAsync(string channel, string message)
    {
        var subscriber = _redis.GetSubscriber();
        // Kết hợp Prefix với tên Channel để tránh đụng hàng
        await subscriber.PublishAsync(RedisChannel.Literal(_prefix + channel), message);
    }

    public async Task SubscribeAsync(string channel, Action<string> handler)
    {
        var subscriber = _redis.GetSubscriber();
        await subscriber.SubscribeAsync(RedisChannel.Literal(_prefix + channel), (redisChannel, value) =>
        {
            handler(value.ToString());
        });
    }


}