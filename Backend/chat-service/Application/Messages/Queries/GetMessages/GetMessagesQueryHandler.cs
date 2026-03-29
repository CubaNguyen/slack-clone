using Application.Common.Interfaces;
using Dapper;
using MediatR;
using Microsoft.EntityFrameworkCore;
using System.Data;
using ChatService.Application.Messages.Queries;
using ChatService.Domain.Common;
using ChatService.Domain.Exceptions;
using ChatService.Application.Common.Interfaces;

namespace ChatService.Application.Messages.Queries.GetMessages;

public class GetMessagesQueryHandler : IRequestHandler<GetMessagesQuery, List<MessageDto>>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;

    public GetMessagesQueryHandler(IApplicationDbContext context, ICurentUserContext userContext)
    {
        _context = context;
        _userContext = userContext;
    }

    public async Task<List<MessageDto>> Handle(GetMessagesQuery request, CancellationToken cancellationToken)
    {
        var currentUserId = _userContext.UserId;

        if (_context is not DbContext dbContext)
        {
            return new List<MessageDto>();
        }

        var connection = dbContext.Database.GetDbConnection();

        // ==========================================
        // 1. QUERY TÌM TIN NHẮN
        // ==========================================
        var sqlMessages = @"
            SELECT id, 
                   channel_id as ChannelId, 
                   user_id as UserId, 
                   content, 
                   created_at as CreatedAt, 
                   type,
                   parent_id as ParentId,
                   reply_count as ReplyCount,        
                   latest_reply_at as LatestReplyAt 
            FROM messages
            WHERE channel_id = @ChannelId
            AND parent_id IS NULL
            AND deleted_at IS NULL
            AND (@IsFirstPage = TRUE OR created_at < @Before::timestamp) 
            ORDER BY created_at DESC
            LIMIT @Limit";

        var messages = await connection.QueryAsync<MessageDto>(sqlMessages, new
        {
            ChannelId = request.ChannelId,
            Before = request.Before,
            IsFirstPage = !request.Before.HasValue,
            Limit = request.Limit
        });

        var messageList = messages.ToList();

        if (!messageList.Any())
        {
            return new List<MessageDto>();
        }

        // ==========================================
        // 2. QUERY TÌM VÀ GOM NHÓM REACTION
        // ==========================================
        var messageIds = messageList.Select(m => m.Id).ToList();

        var sqlReactions = @"
            SELECT 
                message_id as MessageId,
                emoji as Emoji,
                COUNT(*)::int as Count, 
                BOOL_OR(user_id = @CurrentUserId) as HasReacted 
            FROM message_reactions
            WHERE message_id = ANY(@MessageIds)
            GROUP BY message_id, emoji;";

        // ✅ ĐÃ SỬA: Không dùng dynamic nữa, truyền thẳng ReactionRawDto vào
        var reactions = await connection.QueryAsync<ReactionRawDto>(sqlReactions, new
        {
            MessageIds = messageIds,
            CurrentUserId = currentUserId
        });

        // ==========================================
        // 3. RÁP REACTION VÀO MESSAGE
        // ==========================================
        // ✅ ĐÃ SỬA: Bỏ ép kiểu (Guid) nguy hiểm đi
        var reactionLookup = reactions.GroupBy(r => r.MessageId)
                                      .ToDictionary(g => g.Key, g => g.ToList());

        foreach (var msg in messageList)
        {
            if (reactionLookup.TryGetValue(msg.Id, out var msgReactions))
            {
                // ✅ ĐÃ SỬA: Bỏ luôn mấy cái ép kiểu (string), (int), (bool)
                msg.Reactions = msgReactions.Select(r => new ReactionSummaryDto
                {
                    Emoji = r.Emoji,
                    Count = r.Count,
                    HasReacted = r.HasReacted
                }).ToList();
            }
            else
            {
                msg.Reactions = new List<ReactionSummaryDto>();
            }
        }

        Console.WriteLine($"[DEBUG] Đã query thành công {messageList.Count} tin nhắn kèm Reactions.");

        return messageList.Reverse<MessageDto>().ToList();
    }
}

// ==========================================
// ✅ ĐÃ THÊM: Class phụ trợ để Dapper map data an toàn tuyệt đối
// ==========================================
public class ReactionRawDto
{
    public Guid MessageId { get; set; }
    public string Emoji { get; set; } = string.Empty;
    public int Count { get; set; }
    public bool HasReacted { get; set; }
}