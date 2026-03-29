using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class MessageReactionConfiguration : IEntityTypeConfiguration<MessageReaction>
{
    public void Configure(EntityTypeBuilder<MessageReaction> builder)
    {
        builder.ToTable("message_reactions");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.MessageId).HasColumnName("message_id");
        builder.Property(x => x.UserId).HasColumnName("user_id");

        builder.Property(x => x.Emoji)
            .HasColumnName("emoji")
            .HasMaxLength(50)
            .IsRequired();

        builder.Property(x => x.CreatedAt).HasColumnName("created_at");

        // Quan hệ với Message
        builder.HasOne(x => x.Message)
            .WithMany(m => m.MessageReactions)
            .HasForeignKey(x => x.MessageId);
    }
}