using ChatService.Application.Messages.Queries.GetMessages;

namespace ChatService.Application.Common.Models;


// 1. DTO cho Tin nhắn con (Siêu gọn, không chứa parentId hay replyCount)
public class ReplyDto
{
    public Guid Id { get; set; }
    public Guid ChannelId { get; set; }
    public Guid? UserId { get; set; }
    public string Content { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; }
    public int Type { get; set; }
    public List<ReactionSummaryDto> Reactions { get; set; } = new(); // Thêm dòng này
}

// 2. DTO cho Tin nhắn gốc (Kế thừa ReplyDto cho lẹ, thêm 2 trường đếm)
public class ParentMessageDto : ReplyDto
{
    public int ReplyCount { get; set; }
    public DateTime? LatestReplyAt { get; set; }
}

// 3. Metadata bọc ngoài cho sang chảnh
public class ThreadMetadataDto
{
    public int TotalParticipants { get; set; }
    public int TotalReplies { get; set; }
}

// 4. Object tổng trả về
public class ThreadDetailDto
{
    public ParentMessageDto Parent { get; set; } = null!;
    public List<ReplyDto> Replies { get; set; } = new();
    public ThreadMetadataDto Metadata { get; set; } = null!;
}