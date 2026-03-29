namespace ChatService.Domain.Entities
{
    public class ChannelRead
    {
        public Guid Id { get; set; }
        public Guid ChannelId { get; set; }
        public Guid UserId { get; set; }
        public DateTime LastReadAt { get; set; }
        public DateTime UpdatedAt { get; set; }

        public virtual ChannelReplica? Channel { get; set; }
    }
}