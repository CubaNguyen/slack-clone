using Application.Common.Interfaces;
using ChatService.Application.Common.Interfaces;
using ChatService.Domain.Common;
using ChatService.Domain.Exceptions;
using MediatR;
using Microsoft.EntityFrameworkCore;

public class ChannelAuthorizationBehavior<TRequest, TResponse>
    : IPipelineBehavior<TRequest, TResponse> where TRequest : IRequest<TResponse>
{
    private readonly IApplicationDbContext _context;
    private readonly ICurentUserContext _userContext;

    public ChannelAuthorizationBehavior(IApplicationDbContext context, ICurentUserContext userContext)
    {
        _context = context;
        _userContext = userContext;
    }

    public async Task<TResponse> Handle(TRequest request, RequestHandlerDelegate<TResponse> next, CancellationToken cancellationToken)
    {
        // Kiểm tra xem Request này có yêu cầu check quyền Channel không
        if (request is IRequireChannelAuthorization authRequest)
        {
            var currentUserId = _userContext.UserId;

            var isMember = await _context.ChannelMemberReplicas
                .AnyAsync(x => x.ChannelId == authRequest.ChannelId && x.UserId == currentUserId, cancellationToken);

            if (!isMember)
            {
                throw new AppException(ErrorCode.NotInChannel);
            }
        }

        // Nếu hợp lệ hoặc không yêu cầu check, cho đi tiếp đến Handler
        return await next();
    }
}