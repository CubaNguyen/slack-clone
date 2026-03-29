using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class MessagePinConfiguration : IEntityTypeConfiguration<MessagePin>
{
    public void Configure(EntityTypeBuilder<MessagePin> builder)
    {
        builder.ToTable("message_pins");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.MessageId).HasColumnName("message_id");
        builder.Property(x => x.ChannelId).HasColumnName("channel_id");
        builder.Property(x => x.PinnedBy).HasColumnName("pinned_by");
        builder.Property(x => x.PinnedAt).HasColumnName("pinned_at");

        // Quan hệ với Message
        builder.HasOne(x => x.Message)
            .WithMany()
            .HasForeignKey(x => x.MessageId);

        // Quan hệ với Channel
        builder.HasOne(x => x.Channel)
            .WithMany(c => c.MessagePins)
            .HasForeignKey(x => x.ChannelId);
    }
}