
namespace ChatService.Application.Common.dto.Interfaces;

using Confluent.Kafka;

public interface IKafkaConsumer
{
    void Subscribe(string topic, string groupId);
    ConsumeResult<string, string> Consume(CancellationToken ct);

    ConsumeResult<string, string>? Consume(TimeSpan timeout);

    void Commit();
}