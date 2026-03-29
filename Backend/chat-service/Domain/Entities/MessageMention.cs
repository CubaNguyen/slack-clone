using ChatService.Domain.Enums;

namespace ChatService.Domain.Entities
{
    public class MessageMention
    {
        public Guid Id { get; set; }
        public Guid MessageId { get; set; }
        public Guid? MentionedUserId { get; set; } // Nullable theo schema
        public MentionType Type { get; set; }

        public virtual Message? Message { get; set; }
    }
}