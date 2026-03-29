namespace ChatService.Application.Common.Models;

public record ThreadMetadata
{
    public int ReplyCount { get; init; }
    public DateTime LatestReplyAt { get; init; }
    public Guid LatestUserId { get; init; } // Biết ai là người rep cuối
}