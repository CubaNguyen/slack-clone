namespace ChatService.Domain.Entities;

public class ChannelMemberReplica
{
    public Guid ChannelId { get; set; }
    public Guid UserId { get; set; }
    public DateTime JoinedAt { get; set; }

    // Navigation property
    public virtual ChannelReplica Channel { get; set; } = null!;
}