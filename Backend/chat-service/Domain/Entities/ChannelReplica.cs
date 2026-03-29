using ChatService.Domain.Enums;


namespace ChatService.Domain.Entities
{
    public class ChannelReplica
    {
        public Guid Id { get; set; } // ChannelId từ service gốc
        public Guid WorkspaceId { get; set; }
        public string Name { get; set; } = string.Empty;
        public ChannelType Type { get; set; }
        public DateTime? ArchivedAt { get; set; }

        public virtual ICollection<Message> Messages { get; set; } = new List<Message>();
        public virtual ICollection<ChannelRead> ChannelReads { get; set; } = new List<ChannelRead>();
        public virtual ICollection<ScheduledMessage> ScheduledMessages { get; set; } = new List<ScheduledMessage>();
        public virtual ICollection<MessagePin> MessagePins { get; set; } = new List<MessagePin>();

    }
}