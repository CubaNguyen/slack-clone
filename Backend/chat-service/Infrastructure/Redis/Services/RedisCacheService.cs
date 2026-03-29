using ChatService.Application.Common.Interfaces;
using StackExchange.Redis;
using System.Text.Json;

namespace ChatService.Infrastructure.Redis.Services;

public class RedisCacheService : ICacheService
{
    private readonly IConnectionMultiplexer _redis;
    private readonly IDatabase _db;
    private readonly string _prefix = "chat_cache:"; // Prefix riêng để phân biệt với Bus

    public RedisCacheService(IConnectionMultiplexer redis)
    {
        _redis = redis;
        _db = redis.GetDatabase();
    }

    public async Task SetAsync<T>(string key, T value, TimeSpan? expiry = null)
    {
        var jsonData = JsonSerializer.Serialize(value);
        var fullKey = _prefix + key;

        // 1. Chỉ lưu giá trị thôi (Hàm này cực kỳ lành tính, không bao giờ lỗi kiểu dữ liệu)
        await _db.StringSetAsync(fullKey, jsonData);

        // 2. Nếu có truyền thời gian hết hạn thì mới set riêng
        if (expiry.HasValue)
        {
            await _db.KeyExpireAsync(fullKey, expiry);
        }
    }

    public async Task<T?> GetAsync<T>(string key)
    {
        var data = await _db.StringGetAsync(_prefix + key);
        if (data.IsNull) return default; // Check IsNull chuẩn của RedisValue

        // Ép kiểu (string) để JsonSerializer không bị lú
        return JsonSerializer.Deserialize<T>((string)data!);
    }

    public async Task RemoveAsync(string key) => await _db.KeyDeleteAsync(_prefix + key);
}