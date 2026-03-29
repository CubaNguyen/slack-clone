namespace ChatService.Application.Common.dto.Interfaces; // Nhớ check lại namespace cho khớp folder của bạn

public interface IKafkaProducer
{
    Task ProduceAsync(string topic, string key, string message);
}