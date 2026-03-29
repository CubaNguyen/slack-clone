using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class ScheduledMessageConfiguration : IEntityTypeConfiguration<ScheduledMessage>
{
    public void Configure(EntityTypeBuilder<ScheduledMessage> builder)
    {
        builder.ToTable("scheduled_messages");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.ChannelId).HasColumnName("channel_id");
        builder.Property(x => x.UserId).HasColumnName("user_id");
        builder.Property(x => x.Content).HasColumnName("content").IsRequired();
        builder.Property(x => x.ScheduledAt).HasColumnName("scheduled_at");
        builder.Property(x => x.CreatedAt).HasColumnName("created_at");

        // Quan hệ với Channel
        builder.HasOne(x => x.Channel)
            .WithMany(c => c.ScheduledMessages)
            .HasForeignKey(x => x.ChannelId);
    }
}