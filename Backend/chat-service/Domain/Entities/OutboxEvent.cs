namespace ChatService.Domain.Entities
{
    public class OutboxEvent
    {
        public Guid Id { get; set; }
        public string AggregateType { get; set; } = string.Empty;
        public Guid AggregateId { get; set; }
        public string EventType { get; set; } = string.Empty;

        // Payload là chuỗi JSON, nhưng ở domain nó cứ là string
        // Việc mapping thành JSONB là việc của Infra
        public string Payload { get; set; } = string.Empty;

        public DateTime CreatedAt { get; set; }
        public DateTime? ProcessedAt { get; set; }
    }
}