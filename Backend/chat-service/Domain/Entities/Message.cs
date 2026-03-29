using ChatService.Domain.Enums; // Import namespace Enum

namespace ChatService.Domain.Entities
{
    public class Message
    {
        public Guid Id { get; set; }
        public Guid ChannelId { get; set; }
        public Guid UserId { get; set; }
        public string Content { get; set; } = string.Empty;
        public Guid? ParentId { get; set; } // Thread Core

        public MessageType Type { get; set; }

        // Logic reply bác yêu cầu
        public int ReplyCount { get; set; } = 0;
        public DateTime? LatestReplyAt { get; set; }

        public DateTime CreatedAt { get; set; }
        public DateTime? EditedAt { get; set; }
        public DateTime? DeletedAt { get; set; }

        public virtual ChannelReplica? Channel { get; set; }
        public virtual Message? Parent { get; set; }
        public virtual ICollection<Message> Replies { get; set; } = new List<Message>();
        public virtual ICollection<Attachment> Attachments { get; set; } = new List<Attachment>();
        public virtual ICollection<MessageReaction> MessageReactions { get; set; } = new List<MessageReaction>();
    }
}