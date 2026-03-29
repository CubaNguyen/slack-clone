using Dapper;
using MediatR;
using Microsoft.EntityFrameworkCore;
using ChatService.Application.Messages.Queries.GetMessages;
using ChatService.Application.Common.Interfaces;
using Application.Common.Interfaces;

namespace ChatService.Application.Messages.Queries.GetPinnedMessages;

public class GetPinnedMessagesHandler : IRequestHandler<GetPinnedMessagesQuery, List<MessageDto>>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;



    public GetPinnedMessagesHandler(IApplicationDbContext context, ICurentUserContext userContext)
    {
        _context = context;
        _userContext = userContext;
    }

    public async Task<List<MessageDto>> Handle(GetPinnedMessagesQuery request, CancellationToken cancellationToken)
    {
        var currentUserId = _userContext.UserId;
        if (_context is not DbContext dbContext)
        {
            return new List<MessageDto>();
        }

        var connection = dbContext.Database.GetDbConnection();

        // 1. Lấy tin nhắn thông qua bảng JOIN (Vì ông làm bảng riêng)
        var sqlMessages = @"
        SELECT 
            m.id as Id, m.channel_id as ChannelId, m.user_id as UserId, 
            m.content as Content, m.created_at as CreatedAt, m.type as Type,
            m.parent_id as ParentId, m.reply_count as ReplyCount, 
            m.latest_reply_at as LatestReplyAt
        FROM messages m
        INNER JOIN message_pins mp ON m.id = mp.message_id
        WHERE mp.channel_id = @ChannelId
        ORDER BY mp.pinned_at DESC";

        var messages = (await connection.QueryAsync<MessageDto>(sqlMessages, new { ChannelId = request.ChannelId })).ToList();

        if (!messages.Any()) return messages;

        // 2. Lấy Reactions (Đừng quên bước này kẻo nó lại ra mảng rỗng [])
        var messageIds = messages.Select(m => m.Id).ToList();
        var sqlReactions = @"
        SELECT 
            message_id as MessageId, emoji, 
            COUNT(*)::int as Count,
            BOOL_OR(user_id = @CurrentUserId) as HasReacted 
        FROM message_reactions
        WHERE message_id = ANY(@MessageIds)
        GROUP BY message_id, emoji;";

        var reactions = await connection.QueryAsync<ReactionRawDto>(sqlReactions, new
        {
            MessageIds = messageIds,
            CurrentUserId = currentUserId
        });

        // 3. Ráp data (Map reactions vào từng message)
        var reactionLookup = reactions.GroupBy(r => r.MessageId).ToDictionary(g => g.Key, g => g.ToList());
        foreach (var msg in messages)
        {
            msg.Reactions = reactionLookup.TryGetValue(msg.Id, out var r)
                ? r.Select(x => new ReactionSummaryDto { Emoji = x.Emoji, Count = x.Count, HasReacted = x.HasReacted }).ToList()
                : new List<ReactionSummaryDto>();
        }

        return messages;
    }
}