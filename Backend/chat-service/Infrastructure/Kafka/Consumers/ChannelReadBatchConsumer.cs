using Microsoft.Extensions.Hosting;
using Microsoft.EntityFrameworkCore;
using Dapper;
using ChatService.Application.Common.dto.Interfaces;
using ChatService.Application.Messages.Commands.MarkAsRead;
using System.Text.Json;
using Application.Common.Interfaces;
using ChatService.Application.Common.Interfaces;
public class ChannelReadBatchConsumer : BackgroundService
{
    private readonly IServiceProvider _serviceProvider;


    private readonly IKafkaProducer _kafkaProducer;
    private readonly IKafkaConsumer _consumer; // Interface consumer của bạn
    private readonly List<ChannelReadEvent> _batch = new();
    private DateTime _lastFlush = DateTime.UtcNow;

    public ChannelReadBatchConsumer(IServiceProvider serviceProvider, IKafkaConsumer consumer, IKafkaProducer kafkaProducer)
    {
        _serviceProvider = serviceProvider;
        _consumer = consumer;
        _kafkaProducer = kafkaProducer;
    }

    // Thay ruột hàm ExecuteAsync của ông bằng đoạn này:
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _consumer.Subscribe("channel-read-events", "read-receipt-group");
        var jsonOptions = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };

        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                var result = _consumer.Consume(TimeSpan.FromSeconds(1));

                if (result != null)
                {
                    var @event = JsonSerializer.Deserialize<ChannelReadEvent>(result.Value, jsonOptions);
                    if (@event != null) _batch.Add(@event);
                }

                if (_batch.Count >= 100 || (DateTime.UtcNow - _lastFlush).TotalSeconds > 3)
                {
                    if (_batch.Any())
                    {
                        await FlushToDb();
                        // Lưu DB xong thì chốt sổ
                        _consumer.Commit();
                    }
                    _lastFlush = DateTime.UtcNow;
                }
            }
        }
        catch (OperationCanceledException)
        {
            Console.WriteLine("[WORKER] Nhận lệnh tắt app. Đang dọn dẹp ChannelRead...");
        }
        finally
        {
            if (_batch.Any())
            {
                await FlushToDb();
                _consumer.Commit();
            }
        }
    }
    private async Task FlushToDb()

    {
        Console.WriteLine($"[WORKER] từ ChannelReadBatchConsumer --> Đang gom được {_batch.Count} tin nhắn. Chuẩn bị vả vào Postgres...");
        if (!_batch.Any()) return;

        using var scope = _serviceProvider.CreateScope();
        var dbContext = scope.ServiceProvider.GetRequiredService<IApplicationDbContext>();
        var connection = ((DbContext)dbContext).Database.GetDbConnection();

        // [Suy luận]: Dùng SQL Raw 'ON CONFLICT' để Upsert hàng loạt cực nhanh (PostgreSQL)
        var sql = @"
    INSERT INTO channel_reads (id, channel_id, user_id, last_read_at, updated_at)
    VALUES (gen_random_uuid(), @ChannelId, @UserId, @ReadAt, NOW())
    ON CONFLICT (channel_id, user_id) 
    DO UPDATE SET last_read_at = EXCLUDED.last_read_at, updated_at = NOW();";

        try
        {
            await connection.ExecuteAsync(sql, _batch);
            _batch.Clear();
            _lastFlush = DateTime.UtcNow;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[ERROR] từ ChannelReadBatchConsumer Batch lỗi rồi dcm: {ex.Message}");

            // Nếu lỗi cả batch, ta không được bỏ cuộc. 
            // Duyệt từng tin trong batch đó để tìm xem thằng nào là "con sâu làm rầu nồi canh"
            foreach (var item in _batch)
            {
                try
                {
                    // Thử lưu lẻ tẻ từng thằng (để lọc lỗi)
                    await connection.ExecuteAsync(sql, item);
                }
                catch
                {
                    // Thằng này chắc chắn lỗi (ví dụ NULL id nãy) -> Ném vào DLQ
                    Console.WriteLine($"[DLQ] từ ChannelReadBatchConsumer--> Ném tin của User {item.UserId} vào thùng rác DLQ để check sau.");

                    await _kafkaProducer.ProduceAsync(
                        "channel-read-dlq",
                        item.UserId.ToString(),
                        JsonSerializer.Serialize(item)
                    );
                }
            }
            Console.WriteLine($"[DATABASE] từ ChannelReadBatchConsumer --> Đã thực thi SQL Upsert thành công cho lô {_batch.Count} dòng.");
        }
    }
}