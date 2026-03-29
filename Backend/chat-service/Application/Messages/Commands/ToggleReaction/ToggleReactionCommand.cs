using MediatR;
using ChatService.Application.Common.Interfaces;

namespace ChatService.Application.Messages.Commands.ToggleReaction;

public record ToggleReactionCommand(
    Guid ChannelId,
    Guid MessageId,
    string Emoji // Ví dụ: "heart", "thumbsup"
) : IRequest<bool>, IRequireChannelAuthorization;