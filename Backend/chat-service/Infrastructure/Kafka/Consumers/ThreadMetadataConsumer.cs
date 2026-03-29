using Dapper;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;
using ChatService.Application.Common.Interfaces;
using ChatService.Application.Common.dto.Interfaces;

namespace ChatService.Infrastructure.Kafka.Consumers;

public class ThreadMetadataConsumer : BackgroundService
{
    private readonly IServiceProvider _serviceProvider;
    private readonly IKafkaConsumer _consumer;
    private readonly List<MessageRepliedEvent> _batch = new();
    private DateTime _lastFlush = DateTime.UtcNow;
    private readonly int _batchSize = 10;
    private readonly int _flushIntervalSeconds = 5;

    public ThreadMetadataConsumer(IServiceProvider serviceProvider, IKafkaConsumer consumer)
    {
        _serviceProvider = serviceProvider;
        _consumer = consumer;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _consumer.Subscribe("message-reply-events", "thread-metadata-group");
        Console.WriteLine("[WORKER] --> ThreadMetadataConsumer đã khởi động...");

        var jsonOptions = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };

        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                // FIX 1: Dùng Timeout 1s để không bị block luồng
                var result = _consumer.Consume(TimeSpan.FromSeconds(1));

                if (result != null)
                {
                    var @event = JsonSerializer.Deserialize<MessageRepliedEvent>(result.Value, jsonOptions);
                    if (@event != null && @event.ParentId != Guid.Empty)
                    {
                        _batch.Add(@event);
                        Console.WriteLine($"[WORKER] --> Nhận 1 event chuẩn. Parent: {@event.ParentId}. Batch: {_batch.Count}");
                    }
                }

                var timeSinceLastFlush = (DateTime.UtcNow - _lastFlush).TotalSeconds;

                // Điều kiện Flush
                if (_batch.Count >= _batchSize || timeSinceLastFlush >= _flushIntervalSeconds)
                {
                    if (_batch.Any())
                    {
                        await FlushThreadToDb();

                        // FIX 2: Lưu DB xong PHẢI BÁO KAFKA CHỐT SỔ
                        _consumer.Commit();
                    }

                    // FIX 3: Luôn reset đồng hồ dù có data hay không
                    _lastFlush = DateTime.UtcNow;
                }
            }
        }
        catch (OperationCanceledException)
        {
            // Bắt lỗi khi ấn Ctrl+C
            Console.WriteLine("[WORKER] Nhận lệnh tắt app. Đang dọn dẹp ThreadMetadata...");
        }
        finally
        {
            // FIX 4: Tròn vai đến phút cuối, lưu nốt đồ thừa trước khi sập
            if (_batch.Any())
            {
                Console.WriteLine($"[WORKER] Vớt vát lưu nốt {_batch.Count} tin nhắn reply trước khi tắt...");
                await FlushThreadToDb();
                _consumer.Commit();
            }
        }
    }

    private async Task FlushThreadToDb()
    {
        Console.WriteLine($"[WORKER] --> Bắt đầu Flush {_batch.Count} tin nhắn vào Postgres...");

        using var scope = _serviceProvider.CreateScope();
        var dbContext = scope.ServiceProvider.GetRequiredService<IApplicationDbContext>();
        var connection = ((DbContext)dbContext).Database.GetDbConnection();

        var updates = _batch.GroupBy(x => x.ParentId)
                            .Select(g => new
                            {
                                ParentId = g.Key,
                                Increment = g.Count(),
                                LatestAt = g.Max(x => x.CreatedAt)
                            }).ToList();

        var sql = @"
            UPDATE messages 
            SET reply_count = reply_count + @Increment,
                latest_reply_at = @LatestAt
            WHERE id = @ParentId";

        try
        {
            var affectedRows = await connection.ExecuteAsync(sql, updates);
            Console.WriteLine($"[DATABASE] --> Đã cập nhật thành công {affectedRows} dòng tin nhắn cha.");
            _batch.Clear();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[DATABASE ERROR] --> Lưu batch lỗi dcm: {ex.Message}");
        }
    }
}