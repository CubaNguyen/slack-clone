using System.ComponentModel.DataAnnotations;
using ChatService.Domain.Enums;

namespace ChatService.DTOs;

public record SendMessageRequest
{
    /// <summary>
    /// ID của kênh chat
    /// </summary>
    /// <example>a42fdce5-e17c-4dea-a079-999079c68453</example>
    [Required]
    public Guid ChannelId { get; init; }

    /// <summary>
    /// Nội dung tin nhắn
    /// </summary>
    /// <example>Chào mọi người!</example>
    [Required]
    [StringLength(2000)]
    public string Content { get; init; } = string.Empty;

    /// <summary>
    /// Loại tin nhắn
    /// </summary>
    public MessageType Type { get; init; } = MessageType.TEXT;

    /// <summary>
    /// ID tin nhắn cha (nếu có)
    /// </summary>
    public Guid? ParentId { get; init; }
}