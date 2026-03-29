using ChatService.Application.Common.Interfaces;
using MediatR;

namespace Application.Messages.Commands.EditMessage;


public record EditMessageCommand(Guid ChannelId, Guid MessageId, string NewContent) : IRequest<bool>, IRequireChannelAuthorization;