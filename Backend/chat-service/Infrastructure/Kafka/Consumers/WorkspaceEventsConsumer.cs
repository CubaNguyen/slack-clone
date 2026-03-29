using System.Text.Json;
using Confluent.Kafka;
using MediatR;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using ChatService.Infrastructure.Kafka.Models;
using ChatService.Application.Channels.Commands.SyncChannel;
using Infrastructure.Kafka.Models;

namespace ChatService.Infrastructure.Kafka.Consumers
{
    public class WorkspaceEventsConsumer : BackgroundService
    {
        private readonly ILogger<WorkspaceEventsConsumer> _logger;
        private readonly IServiceProvider _serviceProvider;
        private readonly ConsumerConfig _config;
        private const string Topic = "workspace-events";

        public WorkspaceEventsConsumer(ILogger<WorkspaceEventsConsumer> logger, IServiceProvider serviceProvider, IConfiguration configuration)
        {
            _logger = logger;
            _serviceProvider = serviceProvider;
            _config = new ConsumerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"],
                GroupId = "chat-service-group-v2",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = false
            };
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            using var consumer = new ConsumerBuilder<string, string>(_config).Build();
            consumer.Subscribe(Topic);

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = consumer.Consume(stoppingToken);
                    var json = consumeResult.Message.Value;

                    // --- BƯỚC 1: NHÌN TRỘM (INSPECT) ---
                    // Dùng JsonDocument để đọc nhanh cấu trúc mà chưa cần map vào Class
                    using (JsonDocument doc = JsonDocument.Parse(json))
                    {
                        // Lấy ra field "data" -> "aggregateType"
                        // Cấu trúc JSON của bạn: { "data": { "aggregateType": "WORKSPACE", ... } }
                        if (doc.RootElement.TryGetProperty("data", out JsonElement dataElement) &&
                            dataElement.TryGetProperty("aggregateType", out JsonElement typeElement))
                        {
                            string aggregateType = typeElement.GetString();

                            // --- BƯỚC 2: CHECK ĐIỀU KIỆN (FILTER) ---
                            if (aggregateType == "CHANNEL")
                            {
                                // ✅ ĐÚNG LÀ CHANNEL -> XỬ LÝ
                                await ProcessChannelEvent(json, stoppingToken);
                            }
                            else
                            {
                                // 🚫 KHÔNG PHẢI CHANNEL (VD: WORKSPACE, MEMBER...) -> BỎ QUA
                                _logger.LogInformation($"Ignored event type: {aggregateType}");
                            }
                        }
                    }

                    // --- BƯỚC 3: COMMIT ---
                    // Dù xử lý hay bỏ qua thì cũng phải Commit để Kafka biết mình đã đọc qua tin này rồi
                    consumer.Commit(consumeResult);
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error processing Kafka message");
                }
            }
            consumer.Close();
        }

        // Tách hàm xử lý ra cho gọn
        private async Task ProcessChannelEvent(string json, CancellationToken token)
        {
            using (var scope = _serviceProvider.CreateScope())
            {
                var sender = scope.ServiceProvider.GetRequiredService<ISender>();

                // Lúc này mới Deserialize thật sự vào Class ChannelEventData
                var payload = JsonSerializer.Deserialize<IntegrationEventPayload<ChannelEventData>>(json);

                if (payload != null && payload.Data != null)
                {
                    var command = new SyncChannelCommand(
                        payload.Data.Id,
                        payload.Data.WorkspaceId,
                        payload.Data.Name,
                        payload.Data.Type,
                        payload.Data.IsArchived
                    );

                    await sender.Send(command, token);
                    _logger.LogInformation($"Synced channel {payload.Data.Name} successfully.");
                }
            }
        }
    }
}