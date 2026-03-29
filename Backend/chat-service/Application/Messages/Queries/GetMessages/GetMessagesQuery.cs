using ChatService.Application.Common.Interfaces;
using MediatR;

namespace ChatService.Application.Messages.Queries.GetMessages;

public record GetMessagesQuery : IRequest<List<MessageDto>>, IRequireChannelAuthorization
{
    public Guid ChannelId { get; init; }
    public int Limit { get; init; } = 50;
    public DateTime? Before { get; init; }
}

public class ReactionSummaryDto
{
    public string Emoji { get; set; } = string.Empty; // ví dụ: "heart"
    public int Count { get; set; }                    // Tổng số người đã thả
    public bool HasReacted { get; set; }              // Cờ đánh dấu: "Current User có thả cái này không?"
}

public class MessageDto // Đổi sang class nếu record vẫn bị []
{
    public Guid Id { get; set; }
    public Guid ChannelId { get; set; }
    public Guid? UserId { get; set; }
    public string Content { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; }
    public int Type { get; set; }
    public Guid? ParentId { get; set; }
    public int ReplyCount { get; set; }
    public DateTime? LatestReplyAt { get; set; }

    public List<ReactionSummaryDto> Reactions { get; set; } = new();
}