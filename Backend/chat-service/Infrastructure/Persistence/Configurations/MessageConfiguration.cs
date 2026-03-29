using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class MessageConfiguration : IEntityTypeConfiguration<Message>
{
    public void Configure(EntityTypeBuilder<Message> builder)
    {
        builder.ToTable("messages");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.ChannelId).HasColumnName("channel_id");
        builder.Property(x => x.UserId).HasColumnName("user_id");
        builder.Property(x => x.Content).HasColumnName("content").IsRequired();
        builder.Property(x => x.ParentId).HasColumnName("parent_id");

        builder.Property(x => x.Type)
            .HasColumnName("type")
            .HasConversion<int>(); // Lưu Enum MessageType thành int

        builder.Property(x => x.ReplyCount)
            .HasColumnName("reply_count")
            .HasDefaultValue(0);

        builder.Property(x => x.LatestReplyAt).HasColumnName("latest_reply_at");
        builder.Property(x => x.CreatedAt).HasColumnName("created_at");
        builder.Property(x => x.EditedAt).HasColumnName("edited_at");
        builder.Property(x => x.DeletedAt).HasColumnName("deleted_at");

        // Quan hệ với Channel
        builder.HasOne(x => x.Channel)
            .WithMany(c => c.Messages)
            .HasForeignKey(x => x.ChannelId);

        // Quan hệ Self-referencing (Reply tin nhắn)
        builder.HasOne(x => x.Parent)
            .WithMany(m => m.Replies)
            .HasForeignKey(x => x.ParentId)
            .OnDelete(DeleteBehavior.Restrict); // Tránh xóa cha xóa luôn con (hoặc tùy logic bạn)
    }
}