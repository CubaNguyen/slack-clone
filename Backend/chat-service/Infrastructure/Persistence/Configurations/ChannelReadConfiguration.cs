using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class ChannelReadConfiguration : IEntityTypeConfiguration<ChannelRead>
{
    public void Configure(EntityTypeBuilder<ChannelRead> builder)
    {
        builder.ToTable("channel_reads");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.ChannelId).HasColumnName("channel_id");
        builder.Property(x => x.UserId).HasColumnName("user_id");
        builder.Property(x => x.LastReadAt).HasColumnName("last_read_at");
        builder.Property(x => x.UpdatedAt).HasColumnName("updated_at");

        // Relationships
        builder.HasOne(x => x.Channel)
            .WithMany(c => c.ChannelReads)
            .HasForeignKey(x => x.ChannelId);
    }
}