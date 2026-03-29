using MediatR;
using Microsoft.EntityFrameworkCore;
using ChatService.Application.Common.Interfaces;
using ChatService.Domain.Entities;
using System.Text.Json;
using Application.Common.Interfaces;
using ChatService.Infrastructure.Redis.Services;
using ChatService.Application.Common.Models;
using ChatService.Application.Common.Constants;

namespace ChatService.Application.Messages.Commands.TogglePin;

public class TogglePinHandler : IRequestHandler<TogglePinCommand, bool>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;
    private readonly IRedisBusService _redisBus;

    public TogglePinHandler(IApplicationDbContext context, ICurentUserContext userContext, IRedisBusService redisBus)
    {
        _context = context;
        _userContext = userContext;
        _redisBus = redisBus;
    }

    public async Task<bool> Handle(TogglePinCommand request, CancellationToken cancellationToken)
    {
        var currentUserId = _userContext.UserId;

        // 1. Check xem tin nhắn này đã bị ghim trong Channel này chưa
        var existingPin = await _context.MessagePins
            .FirstOrDefaultAsync(p => p.MessageId == request.MessageId, cancellationToken);

        bool isPinned;

        if (existingPin == null)
        {
            // CHƯA GHIM -> TIẾN HÀNH GHIM
            var pin = new MessagePin
            {
                MessageId = request.MessageId,
                ChannelId = request.ChannelId,
                PinnedBy = currentUserId,
                PinnedAt = DateTime.UtcNow
            };
            _context.MessagePins.Add(pin);
            isPinned = true;
        }
        else
        {
            // ĐÃ GHIM -> GỠ GHIM (UNPIN)
            _context.MessagePins.Remove(existingPin);
            isPinned = false;
        }

        await _context.SaveChangesAsync(cancellationToken);
        var pinEvent = RealtimeEvent.Create(EventTypes.Realtime.MessagePinToggled, new
        {
            messageId = request.MessageId,
            isPinned = isPinned,
            channelId = request.ChannelId // Thêm cái này để Go/FE dễ lọc nếu cần
        });
        await _redisBus.PublishAsync(
        $"channel:{request.ChannelId}",
        JsonSerializer.Serialize(pinEvent)
    );

        return isPinned;
    }
}