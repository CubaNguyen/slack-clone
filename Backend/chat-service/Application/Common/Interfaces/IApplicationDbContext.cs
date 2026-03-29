namespace ChatService.Application.Common.Interfaces; // Thêm ChatService. vào đây

using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;

public interface IApplicationDbContext
{
    DbSet<Message> Messages { get; }
    DbSet<Attachment> Attachments { get; }
    DbSet<OutboxEvent> OutboxEvents { get; }

    DbSet<ChannelReplica> ChannelReplicas { get; }
    DbSet<ChannelMemberReplica> ChannelMemberReplicas { get; } // Thêm dòng này

    DbSet<MessageReaction> MessageReactions { get; }

    DbSet<MessageMention> MessageMentions { get; }
    DbSet<MessagePin> MessagePins { get; }
    DbSet<ChannelRead> ChannelReads { get; }
    DbSet<ScheduledMessage> ScheduledMessages { get; }



    Task<int> SaveChangesAsync(CancellationToken cancellationToken);
}