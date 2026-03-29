using ChatService.Application.Messages.Queries.GetMessages;

namespace ChatService.Presentation.Controllers.Dtos;

public record GetMessageResponse
{
    public Guid Id { get; init; }
    public string Content { get; init; } = string.Empty;
    public DateTime CreatedAt { get; init; }
    public int Type { get; init; }
    public Guid? UserId { get; init; }
    public Guid? ParentId { get; init; }
    public int ReplyCount { get; init; }
    public DateTime? LatestReplyAt { get; init; }
    public List<ReactionSummaryDto> Reactions { get; init; } = new();
}