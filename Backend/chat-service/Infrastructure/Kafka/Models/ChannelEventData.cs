using System.Text.Json.Serialization;

namespace ChatService.Infrastructure.Kafka.Models
{
    // Ứng với phần "data" khi event là WORKSPACE/CHANNEL_CREATED
    public class ChannelEventData
    {
        [JsonPropertyName("channelId")]
        public Guid Id { get; set; }

        [JsonPropertyName("workspaceId")]
        public Guid WorkspaceId { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;

        [JsonPropertyName("type")]
        public string Type { get; set; } = "PUBLIC"; // PUBLIC/PRIVATE

        [JsonPropertyName("isArchived")]
        public bool IsArchived { get; set; } = false;
    }
}