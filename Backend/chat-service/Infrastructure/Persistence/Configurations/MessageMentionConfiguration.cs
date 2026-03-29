using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class MessageMentionConfiguration : IEntityTypeConfiguration<MessageMention>
{
    public void Configure(EntityTypeBuilder<MessageMention> builder)
    {
        builder.ToTable("message_mentions");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.MessageId).HasColumnName("message_id");
        builder.Property(x => x.MentionedUserId).HasColumnName("mentioned_user_id");

        builder.Property(x => x.Type)
            .HasColumnName("type")
            .HasConversion<int>(); // MentionType Enum

        // Quan hệ với Message
        builder.HasOne(x => x.Message)
            .WithMany() // Nếu bên Message không cần list Mentions thì để trống
            .HasForeignKey(x => x.MessageId);
    }
}