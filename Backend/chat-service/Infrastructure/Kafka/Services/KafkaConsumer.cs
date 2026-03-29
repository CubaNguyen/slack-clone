using Confluent.Kafka;
using ChatService.Application.Common.Interfaces;
using Microsoft.Extensions.Configuration;
using ChatService.Application.Common.dto.Interfaces;

namespace ChatService.Infrastructure.Kafka.Services;

public class KafkaConsumer : IKafkaConsumer, IDisposable
{
    private IConsumer<string, string> _consumer;
    private readonly IConfiguration _configuration;

    public KafkaConsumer(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public void Subscribe(string topic, string groupId)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = _configuration["Kafka:BootstrapServers"],
            GroupId = groupId, // Lấy từ tham số truyền vào
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = false
        };
        _consumer = new ConsumerBuilder<string, string>(config).Build();
        _consumer.Subscribe(topic);
    }
    public ConsumeResult<string, string> Consume(CancellationToken ct)
    {
        try
        {
            return _consumer.Consume(ct);
        }
        catch
        {
            return null;
        }
    }
    public ConsumeResult<string, string>? Consume(TimeSpan timeout)
    {
        if (_consumer == null) return null;

        try
        {
            // Gọi hàm Consume có sẵn của thư viện Confluent.Kafka
            return _consumer.Consume(timeout);
        }
        catch (ConsumeException ex)
        {
            Console.WriteLine($"[KAFKA ERROR] Lỗi khi đọc tin nhắn: {ex.Error.Reason}");
            return null;
        }
    }

    public void Commit()
    {
        if (_consumer != null)
        {
            try
            {
                // Báo cho Kafka: "Tao xử lý xong đến đoạn này rồi, đừng gửi lại nữa!"
                _consumer.Commit();
            }
            catch (KafkaException e)
            {
                Console.WriteLine($"[KAFKA ERROR] Lỗi khi Commit: {e.Error.Reason}");
            }
        }
    }
    public void Dispose()
    {
        _consumer?.Close();
        _consumer?.Dispose();
    }
}