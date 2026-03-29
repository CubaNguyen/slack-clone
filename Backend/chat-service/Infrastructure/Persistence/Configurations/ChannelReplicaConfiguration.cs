using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class ChannelReplicaConfiguration : IEntityTypeConfiguration<ChannelReplica>
{
    public void Configure(EntityTypeBuilder<ChannelReplica> builder)
    {
        builder.ToTable("channels_replica");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.WorkspaceId).HasColumnName("workspace_id");

        builder.Property(x => x.Name)
            .HasColumnName("name")
            .HasMaxLength(100)
            .IsRequired();

        builder.Property(x => x.Type)
            .HasColumnName("type")
            .HasConversion<int>(); // Lưu Enum dưới dạng int trong DB

        builder.Property(x => x.ArchivedAt).HasColumnName("archived_at");

        // Relationships
        builder.HasMany(x => x.Messages)
            .WithOne(m => m.Channel)
            .HasForeignKey(m => m.ChannelId);

        builder.HasMany(x => x.ChannelReads)
            .WithOne(cr => cr.Channel)
            .HasForeignKey(cr => cr.ChannelId);

        builder.HasMany(x => x.ScheduledMessages)
            .WithOne(sm => sm.Channel)
            .HasForeignKey(sm => sm.ChannelId);
    }
}