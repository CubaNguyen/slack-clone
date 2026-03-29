namespace ChatService.Application.Common.Interfaces;

public class MessageRepliedEvent
{
    public Guid ParentId { get; set; }
    public Guid MessageId { get; set; } // ID của tin nhắn reply
    public DateTime CreatedAt { get; set; }
}