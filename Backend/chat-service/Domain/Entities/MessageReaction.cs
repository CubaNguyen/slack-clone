namespace ChatService.Domain.Entities
{
    public class MessageReaction
    {
        public Guid Id { get; set; }
        public Guid MessageId { get; set; }
        public Guid UserId { get; set; }
        public string Emoji { get; set; } = string.Empty; // Varchar(50)
        public DateTime CreatedAt { get; set; }
        public virtual Message? Message { get; set; }
    }
}