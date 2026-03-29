using MediatR;
using ChatService.Domain.Enums; // Nhớ using Enum

namespace ChatService.Application.Channels.Commands.SyncChannel
{
    // Đây chính là cái DTO bác nói
    // IRequest: Báo hiệu đây là Command ko trả về dữ liệu (void)
    public record SyncChannelCommand(
        Guid Id,
        Guid WorkspaceId,
        string Name,
        string Type, // Nhận string từ Kafka rồi parse sau, hoặc nhận Enum luôn tùy serializer
        bool IsArchived
    ) : IRequest;
}