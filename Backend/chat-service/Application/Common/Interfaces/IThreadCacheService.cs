using ChatService.Application.Common.Models;

namespace ChatService.Application.Common.Interfaces;

public interface IThreadCacheService
{
    /// <summary>
    /// Tăng số lượng reply và cập nhật mốc thời gian mới nhất trên Redis
    /// </summary>
    Task IncrementReplyCountAsync(Guid parentId, Guid userId, DateTime latestReplyAt);
    /// <summary>
    /// Lấy Metadata của thread (count, latest_at) từ Redis
    /// </summary>
    Task<ThreadMetadata?> GetThreadMetadataAsync(Guid parentId);
}