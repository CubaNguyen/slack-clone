using Application.Common.Interfaces;
using ChatService.Application.Common.Constants;
using ChatService.Application.Common.dto.Interfaces;
using ChatService.Application.Common.Interfaces;
using ChatService.Application.Common.Models;
using ChatService.Domain.Common;
using ChatService.Domain.Entities;
using ChatService.Domain.Enums;
using ChatService.Domain.Exceptions;
using ChatService.Infrastructure.Redis.Services;
using MediatR;
using Microsoft.EntityFrameworkCore; // Thêm dòng này vào
using System.Text.Json;

namespace ChatService.Application.Messages.Commands.SendMessage;

public class SendMessageHandler : IRequestHandler<SendMessageCommand, Guid>
{
    private readonly IApplicationDbContext _context;

    private readonly IThreadCacheService _threadCache;

    private readonly IKafkaProducer _kafkaProducer;
    private readonly ICurentUserContext _userContext;

    private readonly IRedisBusService _redisBus;



    public SendMessageHandler(IApplicationDbContext context, ICurentUserContext userContext, IRedisBusService redisBus, IThreadCacheService threadCache, IKafkaProducer kafkaProducer)
    {
        _context = context;
        _userContext = userContext;
        _redisBus = redisBus;
        _threadCache = threadCache;
        _kafkaProducer = kafkaProducer;
    }

    public async Task<Guid> Handle(SendMessageCommand request, CancellationToken cancellationToken)
    {
        Console.WriteLine($"[DEBUG] Nhận Request - Content: {request.Content}, ParentId: {request.ParentId}");
        var currentUserId = _userContext.UserId;

        // --- BƯỚC CHECK QUYỀN (AUTHORIZATION) ---
        // Tìm xem cặp (ChannelId, UserId) này có tồn tại trong bảng replica không
        // Đổi request.UserId thành currentUserId

        // -----------------------------------------
        // 1. Tạo Entity và Lưu vào Postgres
        var message = new Message
        {
            Id = Guid.NewGuid(),
            ChannelId = request.ChannelId,
            UserId = currentUserId,
            Content = request.Content,
            ParentId = request.ParentId, // 👈 Gán ParentId vào đây
            CreatedAt = DateTime.UtcNow,
            Type = request.Type
        };

        _context.Messages.Add(message);
        await _context.SaveChangesAsync(cancellationToken);
        if (message.ParentId.HasValue && message.ParentId.Value != Guid.Empty)
        {
            // 1. Tăng count trên Redis ngay (Đường nhanh)
            await _threadCache.IncrementReplyCountAsync(message.ParentId.Value, message.UserId, message.CreatedAt);

            // 2. Bắn Kafka để Worker update Postgres (Đường chậm)
            var replyEvent = new MessageRepliedEvent
            {
                ParentId = message.ParentId.Value,
                MessageId = message.Id,
                CreatedAt = message.CreatedAt
            };
            await _kafkaProducer.ProduceAsync(
                EventTypes.Background.MessageReplied,
                message.ParentId.ToString(), // Dùng ParentId làm Key để xếp hàng
                JsonSerializer.Serialize(replyEvent)
            );
        }
        // 2. Chuẩn bị Data và Publish sang Redis cho Go Service
        var redisEvent = RealtimeEvent.Create(EventTypes.Realtime.MessageCreated, new
        {
            messageId = message.Id,
            channelId = message.ChannelId,
            userId = message.UserId,
            content = message.Content,
            parentId = message.ParentId,
            createdAt = message.CreatedAt,
            type = message.Type.ToString(),
            typeValue = (int)message.Type
        });

        // Console.WriteLine($"Publishing to Redis: {JsonSerializer.Serialize(redisPayload)}");
        // Publish vào channel của Redis (Sử dụng service bạn đã viết)
        await _redisBus.PublishAsync($"channel:{request.ChannelId}", JsonSerializer.Serialize(redisEvent));

        return message.Id;
    }
}