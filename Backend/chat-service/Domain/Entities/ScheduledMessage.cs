namespace ChatService.Domain.Entities
{
    public class ScheduledMessage
    {
        public Guid Id { get; set; }
        public Guid ChannelId { get; set; }
        public Guid UserId { get; set; }
        public string Content { get; set; } = string.Empty;
        public DateTime ScheduledAt { get; set; }
        public DateTime CreatedAt { get; set; }

        public virtual ChannelReplica? Channel { get; set; }
    }
}