using Application.Common.Interfaces;
using ChatService.Application.Common.Interfaces;
using ChatService.Domain.Entities; // 👈 Quan trọng: Trỏ về Domain mới
using ChatService.Domain.Enums;
using Microsoft.EntityFrameworkCore;
using System.Reflection;

namespace ChatService.Infrastructure.Persistence;

public class ChatDbContext : DbContext, IApplicationDbContext
{
    public ChatDbContext(DbContextOptions<ChatDbContext> options) : base(options) { }

    // --- Khai báo DbSet (Dùng class từ Domain, xóa chữ DbEntity) ---
    public DbSet<Message> Messages { get; set; }
    public DbSet<MessageReaction> MessageReactions { get; set; }
    public DbSet<MessageMention> MessageMentions { get; set; }
    public DbSet<MessagePin> MessagePins { get; set; }
    public DbSet<Attachment> Attachments { get; set; }
    public DbSet<ChannelRead> ChannelReads { get; set; }
    public DbSet<ScheduledMessage> ScheduledMessages { get; set; }
    public DbSet<OutboxEvent> OutboxEvents { get; set; }
    public DbSet<ChannelReplica> ChannelReplicas { get; set; }
    public DbSet<ChannelMemberReplica> ChannelMemberReplicas { get; set; } // KIỂM TRA KỸ DÒNG NÀY
    // Triển khai SaveChangesAsync cho IApplicationDbContext
    public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
    {
        return await base.SaveChangesAsync(cancellationToken);
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.Entity<Message>().HasQueryFilter(m => m.DeletedAt == null);

        modelBuilder.Entity<Message>(entity =>
 {
     entity.Property(e => e.Type)
           .HasColumnType("text") // Khai báo rõ ràng với EF là trong DB nó là text
           .HasConversion(
               // 1. Convert từ Enum sang String để lưu xuống DB
               v => ((int)v).ToString(),

               // 2. Convert từ String trong DB sang Enum để C# xài
               v => (MessageType)int.Parse(v ?? "0")
           );
     // Quét tất cả file Configuration trong folder Configurations bạn vừa tạo
     modelBuilder.ApplyConfigurationsFromAssembly(Assembly.GetExecutingAssembly());
 });
    }
}