using ChatService.Application.Common.Interfaces;
using MediatR;
namespace ChatService.Application.Channels.Commands.MarkAsRead;

public record MarkAsReadCommand(Guid ChannelId) : IRequest<Unit>, IRequireChannelAuthorization;