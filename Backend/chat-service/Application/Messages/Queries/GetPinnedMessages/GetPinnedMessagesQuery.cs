using ChatService.Application.Common.Interfaces;
using ChatService.Application.Messages.Queries.GetMessages;
using MediatR;

public record GetPinnedMessagesQuery(Guid ChannelId) : IRequest<List<MessageDto>>, IRequireChannelAuthorization;