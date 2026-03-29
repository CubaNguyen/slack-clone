
using Application.Common.Interfaces;
using ChatService.Application.Common.Interfaces;
using ChatService.Infrastructure.Redis.Services;
using MediatR;
using System.Text.Json;

using Microsoft.EntityFrameworkCore;
using Application.Messages.Commands.EditMessage;

public class EditMessageHandler : IRequestHandler<EditMessageCommand, bool>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;
    private readonly IRedisBusService _redisBus;

    public EditMessageHandler(IApplicationDbContext context, ICurentUserContext userContext, IRedisBusService redisBus)
    {
        _context = context;
        _userContext = userContext;
        _redisBus = redisBus;
    }

    public async Task<bool> Handle(EditMessageCommand request, CancellationToken cancellationToken)
    {
        var currentUserId = _userContext.UserId;

        // 1. Chỉ lấy Id và UserId để check quyền, né cột 'type' ra cho lành dcm
        var messageInfo = await _context.Messages
            .Where(m => m.Id == request.MessageId)
            .Select(m => new { m.UserId, m.DeletedAt })
            .FirstOrDefaultAsync(cancellationToken);

        if (messageInfo == null || messageInfo.DeletedAt != null) return false;

        // 2. Security: Chỉ thằng viết tin nhắn mới được sửa
        if (messageInfo.UserId != currentUserId)
            throw new UnauthorizedAccessException("Định sửa tin nhắn nhà người ta à dcm?");

        // 3. Update nội dung và đánh dấu Edited (Dùng ExecuteUpdate cho nhanh và né Mapping lỗi)
        // Nếu không có .NET 7, bạn dùng SQL Raw cho nó chất
        if (_context is not DbContext dbContext) throw new Exception("Error dcm!");

        var rowsAffected = await dbContext.Database.ExecuteSqlRawAsync(
            "UPDATE messages SET content = {0}, updated_at = {1} WHERE id = {2}",
            request.NewContent, DateTime.UtcNow, request.MessageId, cancellationToken);

        if (rowsAffected > 0)
        {
            // 4. Bắn Real-time để FE đổi text và hiện chữ (edited)
            await _redisBus.PublishAsync($"channel:{request.ChannelId}", JsonSerializer.Serialize(new
            {
                type = "MESSAGE_EDITED",
                data = new { messageId = request.MessageId, content = request.NewContent }
            }));
            return true;
        }

        return false;
    }
}