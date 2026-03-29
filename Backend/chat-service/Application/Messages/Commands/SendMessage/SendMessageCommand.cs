using ChatService.Application.Common.Interfaces;
using ChatService.Domain.Enums;
using MediatR;

namespace ChatService.Application.Messages.Commands.SendMessage;

public record SendMessageCommand(
    Guid ChannelId,
    string Content,
    MessageType Type = MessageType.TEXT,
    Guid? ParentId = null
) : IRequest<Guid>, IRequireChannelAuthorization;