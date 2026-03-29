using ChatService.Application.Common.Interfaces;
using MediatR;
namespace ChatService.Application.Messages.Commands.TogglePin;

public record TogglePinCommand(Guid ChannelId, Guid MessageId) : IRequest<bool>, IRequireChannelAuthorization;