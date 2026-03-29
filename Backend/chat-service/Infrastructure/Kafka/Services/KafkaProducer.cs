using Confluent.Kafka;
using ChatService.Application.Common.Interfaces;
using Microsoft.Extensions.Configuration;
using ChatService.Application.Common.dto.Interfaces;

namespace ChatService.Infrastructure.Kafka.Services;

public class KafkaProducer : IKafkaProducer, IDisposable
{
    private readonly IProducer<string, string> _producer;

    public KafkaProducer(IConfiguration configuration)
    {
        var config = new ProducerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"] // "localhost:9092"
        };
        _producer = new ProducerBuilder<string, string>(config).Build();
    }

    public async Task ProduceAsync(string topic, string key, string message)
    {
        var kafkaMessage = new Message<string, string> { Key = key, Value = message };
        await _producer.ProduceAsync(topic, kafkaMessage);
    }

    public void Dispose()
    {
        _producer?.Flush();
        _producer?.Dispose();
    }
}