

namespace ChatService.DTOs;

public record GetMessagesRequest
{
    /// <summary>
    /// Mốc thời gian để lấy các tin nhắn cũ hơn.
    /// </summary>
    /// <example>2026-03-12T14:34:19.510Z</example>
    public DateTime? Before { get; init; }

    /// <example>50</example>
    public int Limit { get; init; } = 50;
}