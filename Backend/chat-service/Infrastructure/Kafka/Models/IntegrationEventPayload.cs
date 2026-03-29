using System.Text.Json.Serialization;

namespace Infrastructure.Kafka.Models
{
    // Class tổng ứng với cấu trúc JSONB bạn đưa
    public class IntegrationEventPayload<TData>
    {
        [JsonPropertyName("meta")]
        public EventMeta Meta { get; set; } = new();

        [JsonPropertyName("data")]
        public TData Data { get; set; } // Generic để tái sử dụng cho nhiều loại event
    }

    public class EventMeta
    {
        [JsonPropertyName("event_id")]
        public Guid EventId { get; set; }

        [JsonPropertyName("trace_id")]
        public string TraceId { get; set; } = string.Empty;

        [JsonPropertyName("actor")]
        public EventActor Actor { get; set; } = new();

        [JsonPropertyName("occurred_at")]
        public DateTime OccurredAt { get; set; }
    }

    public class EventActor
    {
        [JsonPropertyName("user_id")]
        public Guid UserId { get; set; }

        [JsonPropertyName("role")]
        public string Role { get; set; } = string.Empty;
    }
}