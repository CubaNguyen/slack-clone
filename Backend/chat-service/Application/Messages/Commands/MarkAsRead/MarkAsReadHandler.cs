using Application.Common.Interfaces;
using ChatService.Application.Channels.Commands.MarkAsRead;
using ChatService.Application.Common.Interfaces;
using ChatService.Infrastructure.Redis.Services;
using MediatR;
using System.Text.Json;
using Microsoft.Extensions.Logging;
using ChatService.Application.Common.dto.Interfaces;
using ChatService.Application.Common.Constants; // Thêm cái này

public class MarkAsReadHandler : IRequestHandler<MarkAsReadCommand, Unit>
{
    private readonly ICurentUserContext _userContext;
    private readonly IKafkaProducer _kafkaProducer;

    private readonly ICacheService _redisCache;
    private readonly ILogger<MarkAsReadHandler> _logger; // Khai báo Logger

    public MarkAsReadHandler(
        ICurentUserContext userContext,
        IKafkaProducer kafkaProducer,
        ICacheService redisCache,
        ILogger<MarkAsReadHandler> logger) // Inject Logger
    {
        _userContext = userContext;
        _kafkaProducer = kafkaProducer;
        _redisCache = redisCache;
        _logger = logger;
    }

    public async Task<Unit> Handle(MarkAsReadCommand request, CancellationToken cancellationToken)
    {
        var userId = _userContext.UserId;
        var channelId = request.ChannelId;
        var now = DateTime.UtcNow;

        _logger.LogInformation("--- [MarkAsRead] Processing for User: {UserId}, Channel: {ChannelId} ---", userId, request.ChannelId);

        try
        {
            // 1. Lưu Key-Value vào Redis (Hot Cache)
            var cacheKey = $"channel_read:{channelId}:{userId}";


            Console.WriteLine($"[REDIS] --> Đang lưu cache với Key: {cacheKey} ");
            await _redisCache.SetAsync(cacheKey, now.ToString("O"));
            _logger.LogDebug("Saved hot cache to Redis for Key: {RedisKey}", cacheKey);

            // 2. Bắn vào Kafka (Async Write-Behind)
            var payload = new
            {
                ChannelId = request.ChannelId,
                UserId = userId,
                ReadAt = now
            };

            var message = JsonSerializer.Serialize(payload);

            // Sử dụng UserId làm Partition Key để đảm bảo thứ tự xử lý của 1 user luôn chuẩn
            await _kafkaProducer.ProduceAsync(
                EventTypes.Background.ChannelRead,
                userId.ToString(),
                message
            );

            _logger.LogInformation("Successfully published ReadReceipt event to Kafka for User: {UserId}", userId);
        }
        catch (Exception ex)
        {
            // [Suy luận]: Trong High Load, nếu lỗi log ở đây cực kỳ quan trọng để biết hệ thống đang nghẽn ở đâu
            _logger.LogError(ex, "FAILED to process MarkAsRead for User: {UserId} in Channel: {ChannelId}. Error: {Message}",
                userId, request.ChannelId, ex.Message);

            // Tùy nghiệp vụ: có thể throw tiếp hoặc nuốt lỗi để User không thấy lỗi 500
            // throw; 
        }
        Console.WriteLine($"[API] --> Nhận lệnh MarkAsRead: Channel {request.ChannelId} bởi User {userId}");
        Console.WriteLine($"[REDIS] --> Đã lưu cache trạng thái đọc.");
        Console.WriteLine($"[KAFKA] --> Đã bắn event vào topic 'channel-read-events'");
        return Unit.Value;
    }
}