using Dapper;
using MediatR;
using Microsoft.EntityFrameworkCore;
using ChatService.Application.Messages.Queries.GetMessages;
using ChatService.Application.Common.Interfaces;
using ChatService.Application.Common.Models;
using ChatService.Domain.Exceptions;
using System.Data;
using Application.Common.Interfaces;

namespace ChatService.Application.Messages.Queries.GetThreadMessages;

// [QUAN TRỌNG]: Đảm bảo IRequestHandler trả về ThreadDetailDto chứ không phải List
public class GetThreadMessagesQueryHandler : IRequestHandler<GetThreadMessagesQuery, ThreadDetailDto>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;

    public GetThreadMessagesQueryHandler(IApplicationDbContext context, ICurentUserContext userContext)
    {
        _context = context;
        _userContext = userContext;
    }

    public async Task<ThreadDetailDto?> Handle(GetThreadMessagesQuery request, CancellationToken cancellationToken)
    {
        // 1. Kiểm tra User Context trước
        if (_userContext == null) throw new Exception("UserContext is missing!");
        var currentUserId = _userContext.UserId;

        if (_context is not DbContext efContext)
            throw new Exception("Lỗi DbContext!");

        var connection = efContext.Database.GetDbConnection();

        var sql = @"
        -- Query 1: Lấy tin nhắn gốc
        SELECT id, channel_id as ChannelId, user_id as UserId, content, 
               created_at as CreatedAt, type, 
               reply_count as ReplyCount, latest_reply_at as LatestReplyAt
        FROM messages WHERE id = @ParentId;

        -- Query 2: Lấy danh sách Replies
        SELECT id, channel_id as ChannelId, user_id as UserId, content, 
               created_at as CreatedAt, type
        FROM messages WHERE parent_id = @ParentId ORDER BY created_at ASC;

        -- Query 3: Lấy TẤT CẢ Reactions
        SELECT 
            message_id as MessageId,
            emoji as Emoji,
            COUNT(*)::int as Count,
            BOOL_OR(user_id = @CurrentUserId) as HasReacted 
        FROM message_reactions
        WHERE message_id = @ParentId OR message_id IN (SELECT id FROM messages WHERE parent_id = @ParentId)
        GROUP BY message_id, emoji;";

        using var multi = await connection.QueryMultipleAsync(sql, new
        {
            ParentId = request.ParentId,
            CurrentUserId = currentUserId
        });

        // 2. Đọc Parent và check ngay lập tức
        var parent = await multi.ReadFirstOrDefaultAsync<ParentMessageDto>();
        if (parent == null) return null;

        // 3. Đọc Replies và Reactions an toàn
        var repliesRaw = await multi.ReadAsync<ReplyDto>();
        var replies = (repliesRaw ?? Enumerable.Empty<ReplyDto>()).ToList();

        var allReactionsRaw = await multi.ReadAsync<ReactionRawDto>();
        var allReactions = (allReactionsRaw ?? Enumerable.Empty<ReactionRawDto>()).ToList();

        var reactionLookup = allReactions.GroupBy(r => r.MessageId)
                                         .ToDictionary(g => g.Key, g => g.ToList());

        // --- Ráp Reaction cho thằng Cha ---
        parent.Reactions = reactionLookup.TryGetValue(parent.Id, out var pReacts)
            ? pReacts.Select(r => new ReactionSummaryDto { Emoji = r.Emoji, Count = r.Count, HasReacted = r.HasReacted }).ToList()
            : new List<ReactionSummaryDto>();

        // --- Ráp Reaction cho đống thằng Con ---
        foreach (var reply in replies)
        {
            reply.Reactions = reactionLookup.TryGetValue(reply.Id, out var rReacts)
                ? rReacts.Select(r => new ReactionSummaryDto { Emoji = r.Emoji, Count = r.Count, HasReacted = r.HasReacted }).ToList()
                : new List<ReactionSummaryDto>();
        }

        // 4. Khởi tạo Object cuối cùng (Đảm bảo không property nào bị null)
        return new ThreadDetailDto
        {
            Parent = parent,
            Replies = replies,
            Metadata = new ThreadMetadataDto
            {
                TotalParticipants = replies.Where(r => r.UserId != Guid.Empty).Select(r => r.UserId).Distinct().Count(),
                TotalReplies = replies.Count
            }
        };
    }
}