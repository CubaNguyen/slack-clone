using MediatR;
using ChatService.Application.Messages.Queries.GetMessages;
using ChatService.Application.Common.Interfaces;
using ChatService.Application.Common.Models;

namespace ChatService.Application.Messages.Queries.GetThreadMessages;



public record GetThreadMessagesQuery(Guid ChannelId, Guid ParentId)
    // Sửa List<ThreadDetailDto> thành ThreadDetailDto?
    : IRequest<ThreadDetailDto?>, IRequireChannelAuthorization;