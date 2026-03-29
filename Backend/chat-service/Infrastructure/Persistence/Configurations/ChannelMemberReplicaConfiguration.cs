using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class ChannelMemberReplicaConfiguration : IEntityTypeConfiguration<ChannelMemberReplica>
{
    public void Configure(EntityTypeBuilder<ChannelMemberReplica> builder)
    {
        // Tên bảng trong DB
        builder.ToTable("channel_members_replica");

        // Vì đây là bảng trung gian, ta dùng Composite Key (Khóa chính kết hợp)
        builder.HasKey(x => new { x.ChannelId, x.UserId });

        builder.Property(x => x.ChannelId).HasColumnName("channel_id");
        builder.Property(x => x.UserId).HasColumnName("user_id");

        builder.Property(x => x.JoinedAt)
            .HasColumnName("joined_at")
            .HasDefaultValueSql("CURRENT_TIMESTAMP");

        // Cấu hình quan hệ với bảng ChannelReplica
        builder.HasOne(x => x.Channel)
            .WithMany() // Nếu bên ChannelReplica bạn không tạo ICollection<ChannelMemberReplica> thì để trống
            .HasForeignKey(x => x.ChannelId)
            .OnDelete(DeleteBehavior.Cascade);

        // Index để tìm kiếm theo User cho nhanh
        builder.HasIndex(x => x.UserId);
    }
}