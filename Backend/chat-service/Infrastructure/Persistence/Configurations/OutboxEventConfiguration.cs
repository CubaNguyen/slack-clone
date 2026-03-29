using ChatService.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace ChatService.Infrastructure.Persistence.Configurations;

public class OutboxEventConfiguration : IEntityTypeConfiguration<OutboxEvent>
{
    public void Configure(EntityTypeBuilder<OutboxEvent> builder)
    {
        builder.ToTable("outbox_events");

        builder.HasKey(x => x.Id);
        builder.Property(x => x.Id).HasColumnName("id");

        builder.Property(x => x.AggregateType).HasColumnName("aggregate_type").IsRequired();
        builder.Property(x => x.AggregateId).HasColumnName("aggregate_id");
        builder.Property(x => x.EventType).HasColumnName("event_type").IsRequired();

        // Cấu hình kiểu JSONB cho Postgres
        builder.Property(x => x.Payload)
            .HasColumnName("payload")
            .HasColumnType("jsonb")
            .IsRequired();

        builder.Property(x => x.CreatedAt).HasColumnName("created_at");
        builder.Property(x => x.ProcessedAt).HasColumnName("processed_at");
    }
}