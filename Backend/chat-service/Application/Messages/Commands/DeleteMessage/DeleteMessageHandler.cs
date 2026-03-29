using MediatR;
using Microsoft.EntityFrameworkCore;
using ChatService.Application.Common.Interfaces;
using Application.Common.Interfaces;
using ChatService.Infrastructure.Redis.Services;
using System.Text.Json;
using ChatService.Application.Common.Models;
using ChatService.Application.Common.Constants;

namespace ChatService.Application.Messages.Commands.DeleteMessage;

public class DeleteMessageHandler : IRequestHandler<DeleteMessageCommand, bool>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;
    private readonly IRedisBusService _redisBus;



    public DeleteMessageHandler(IApplicationDbContext context, ICurentUserContext userContext, IRedisBusService redisBus)
    {
        _context = context;
        _userContext = userContext;
        _redisBus = redisBus;
    }
    public async Task<bool> Handle(DeleteMessageCommand request, CancellationToken cancellationToken)
    {
        var currentUserId = _userContext.UserId;

        // 1. Chỉ lấy những trường CẦN THIẾT, né cái cột 'type' chết tiệt kia ra dcm
        var messageData = await _context.Messages
            .Where(m => m.Id == request.MessageId)
            .Select(m => new { m.Id, m.UserId, m.DeletedAt }) // Né cột Type ở đây
            .FirstOrDefaultAsync(cancellationToken);

        if (messageData == null || messageData.DeletedAt != null)
            return false;

        // 2. Check quyền
        if (messageData.UserId != currentUserId)
        {
            throw new UnauthorizedAccessException("Không có quyền xóa tin nhắn này!");
        }

        // 3. Sử dụng ExecuteUpdateAsync (Có từ .NET 7) để update thẳng xuống DB
        // Cách này KHÔNG CẦN load cả Entity lên, nên nó không bao giờ đụng vào cột Type
        if (_context is not DbContext dbContext) throw new Exception("Không thể ép kiểu DbContext");

        using var transaction = await dbContext.Database.BeginTransactionAsync(cancellationToken);
        try
        {
            // A. Soft Delete trực tiếp trong DB
            await _context.Messages
                .Where(m => m.Id == request.MessageId)
                .ExecuteUpdateAsync(s => s
                    .SetProperty(m => m.DeletedAt, DateTime.UtcNow)
                    .SetProperty(m => m.Content, "Tin nhắn này đã bị xóa."), cancellationToken);

            // B. Xóa Reactions
            await _context.MessageReactions
                .Where(r => r.MessageId == request.MessageId)
                .ExecuteDeleteAsync(cancellationToken);

            // C. Xóa Pin
            await _context.MessagePins
                .Where(p => p.MessageId == request.MessageId)
                .ExecuteDeleteAsync(cancellationToken);

            // Không cần SaveChangesAsync nữa vì ExecuteUpdate/Delete nó chạy trực tiếp rồi
            await transaction.CommitAsync(cancellationToken);
            var payload = RealtimeEvent.Create(EventTypes.Realtime.MessageDeleted, new
            {
                messageId = request.MessageId
            });
            // 4. Bắn Real-time
            await _redisBus.PublishAsync(
    $"channel:{request.ChannelId}",
    JsonSerializer.Serialize(payload)
);

            return true;
        }
        catch (Exception)
        {
            await transaction.RollbackAsync(cancellationToken);
            throw;
        }
    }
}