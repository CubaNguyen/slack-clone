namespace ChatService.Application.Common.Interfaces;

public interface IRequireChannelAuthorization
{
    Guid ChannelId { get; }
}