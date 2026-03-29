namespace ChatService.Infrastructure.Persistence.Configurations;

using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;


public class AttachmentConfiguration : IEntityTypeConfiguration<Attachment>
{
    public void Configure(EntityTypeBuilder<Attachment> builder)
    {
        builder.ToTable("attachments");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");
        builder.Property(x => x.MessageId).HasColumnName("message_id");

        builder.Property(x => x.FileName)
            .HasColumnName("file_name")
            .HasMaxLength(255);

        builder.Property(x => x.FileType)
            .HasColumnName("file_type")
            .HasMaxLength(100);

        builder.Property(x => x.FileUrl)
            .HasColumnName("file_url")
            .HasColumnType("text");

        builder.Property(x => x.FileSize).HasColumnName("file_size");
        builder.Property(x => x.CreatedAt).HasColumnName("created_at");

        // Relationships
        builder.HasOne(x => x.Message)
            .WithMany(m => m.Attachments)
            .HasForeignKey(x => x.MessageId);
    }
}