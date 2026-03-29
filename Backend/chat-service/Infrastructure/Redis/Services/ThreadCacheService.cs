namespace ChatService.Infrastructure.Redis.Services;

using ChatService.Application.Common.Models;
using StackExchange.Redis;
using ChatService.Application.Common.Interfaces;

public class ThreadCacheService : IThreadCacheService
{
    private readonly IDatabase _db;
    private readonly string _prefix = "chat_cache:thread:"; // Thêm prefix cho đồng bộ với các phần trước

    public ThreadCacheService(IConnectionMultiplexer redis)
    {
        _db = redis.GetDatabase();
    }

    public async Task IncrementReplyCountAsync(Guid parentId, Guid userId, DateTime latestReplyAt)
    {
        var key = _prefix + parentId.ToString();

        // Batch giúp tiết kiệm Network Round-trip
        var batch = _db.CreateBatch();

        // 1. Tăng số lượng rep
        _ = batch.HashIncrementAsync(key, "reply_count", 1);

        // 2. Cập nhật thời gian mới nhất (Định dạng ISO "O")
        _ = batch.HashSetAsync(key, "latest_reply_at", latestReplyAt.ToString("O"));

        // 3. Lưu luôn thằng vừa nhắn để FE hiện avatar cho "oách"
        _ = batch.HashSetAsync(key, "latest_user_id", userId.ToString());

        batch.Execute();
    }

    public async Task<ThreadMetadata?> GetThreadMetadataAsync(Guid parentId)
    {
        var key = _prefix + parentId.ToString();
        var data = await _db.HashGetAllAsync(key);

        if (data.Length == 0) return null;

        // Dùng helper để convert HashEntry[] sang Dictionary cho dễ lấy
        var dict = data.ToDictionary(x => x.Name.ToString(), x => x.Value.ToString());

        return new ThreadMetadata
        {
            ReplyCount = dict.ContainsKey("reply_count") ? int.Parse(dict["reply_count"]) : 0,
            LatestReplyAt = dict.ContainsKey("latest_reply_at")
                ? DateTime.Parse(dict["latest_reply_at"])
                : DateTime.MinValue,
            LatestUserId = dict.ContainsKey("latest_user_id")
                ? Guid.Parse(dict["latest_user_id"])
                : Guid.Empty
        };
    }
}