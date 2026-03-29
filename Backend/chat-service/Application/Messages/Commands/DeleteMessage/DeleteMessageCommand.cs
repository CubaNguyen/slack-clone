using ChatService.Application.Common.Interfaces;
using MediatR;
namespace ChatService.Application.Messages.Commands.DeleteMessage;

public record DeleteMessageCommand(Guid ChannelId, Guid MessageId) : IRequest<bool>, IRequireChannelAuthorization;