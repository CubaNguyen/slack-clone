namespace ChatService.Domain.Entities
{
    public class MessagePin
    {
        public Guid Id { get; set; }
        public Guid MessageId { get; set; }
        public Guid ChannelId { get; set; }
        public Guid PinnedBy { get; set; }
        public DateTime PinnedAt { get; set; }

        public virtual Message? Message { get; set; }
        public virtual ChannelReplica? Channel { get; set; }
    }
}