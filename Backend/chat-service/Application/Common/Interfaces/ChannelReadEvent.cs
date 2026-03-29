namespace ChatService.Application.Messages.Commands.MarkAsRead; // Để chung namespace với Handler cho dễ tìm

public class ChannelReadEvent
{
    public Guid ChannelId { get; set; }
    public Guid UserId { get; set; }
    public DateTime ReadAt { get; set; }
}