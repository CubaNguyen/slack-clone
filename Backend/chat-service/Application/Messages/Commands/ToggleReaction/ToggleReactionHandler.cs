using MediatR;
using Microsoft.EntityFrameworkCore;
using ChatService.Application.Common.Interfaces;
using ChatService.Domain.Entities;
using System.Text.Json;
using Application.Common.Interfaces;
using ChatService.Infrastructure.Redis.Services;
using ChatService.Application.Common.Constants;
using ChatService.Application.Common.Models;

namespace ChatService.Application.Messages.Commands.ToggleReaction;

public class ToggleReactionHandler : IRequestHandler<ToggleReactionCommand, bool>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;
    private readonly IRedisBusService _redisBus;

    public ToggleReactionHandler(
        IApplicationDbContext context,
        ICurentUserContext userContext,
        IRedisBusService redisBus)
    {
        _context = context;
        _userContext = userContext;
        _redisBus = redisBus;
    }
    public async Task<bool> Handle(ToggleReactionCommand request, CancellationToken cancellationToken)
    {
        var userId = _userContext.UserId;

        // 1. Tìm cái Reaction hiện tại của User này trên tin nhắn này (bất kể là Emoji gì)
        var currentReaction = await _context.MessageReactions
            .FirstOrDefaultAsync(r => r.MessageId == request.MessageId
                                   && r.UserId == userId, cancellationToken);

        bool isAdded;
        string? oldEmoji = null;

        if (currentReaction == null)
        {
            // Chưa có cái nào -> Thêm mới cái request.Emoji
            _context.MessageReactions.Add(new MessageReaction
            {
                Id = Guid.NewGuid(),
                MessageId = request.MessageId,
                UserId = userId,
                Emoji = request.Emoji,
                CreatedAt = DateTime.UtcNow
            });
            isAdded = true;
        }
        else if (currentReaction.Emoji == request.Emoji)
        {
            // Bấm trùng cái đang có -> Xóa bỏ (Undo)
            _context.MessageReactions.Remove(currentReaction);
            isAdded = false;
        }
        else
        {
            // Bấm cái MỚI khi đang có cái CŨ -> Đổi Emoji
            oldEmoji = currentReaction.Emoji; // Lưu lại để báo cho FE xóa cái cũ
            currentReaction.Emoji = request.Emoji;
            currentReaction.CreatedAt = DateTime.UtcNow;
            isAdded = true;
        }

        await _context.SaveChangesAsync(cancellationToken);
        var reactionData = new
        {
            messageId = request.MessageId,
            userId = userId,
            // Nếu là thêm mới/đổi: có newEmoji. Nếu là gỡ: newEmoji = null
            newEmoji = isAdded ? request.Emoji : null,
            // Nếu là đổi: có removedEmoji cũ. Nếu là gỡ: removedEmoji chính là cái vừa bấm
            removedEmoji = oldEmoji ?? (isAdded ? null : request.Emoji)
        };
        // 2. Bắn Real-time (Gửi cả info cái cũ và cái mới để FE xử lý mượt)
        var redisEvent = RealtimeEvent.Create(
            EventTypes.Realtime.ReactionUpdated,
            reactionData
        );

        await _redisBus.PublishAsync(
            $"channel:{request.ChannelId}",
            JsonSerializer.Serialize(redisEvent)
        );

        return isAdded;
    }
}