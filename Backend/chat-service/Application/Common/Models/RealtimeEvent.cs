namespace ChatService.Application.Common.Models;

public class RealtimeEvent
{
    public string Event { get; set; } = null!;
    public object Data { get; set; } = null!;

    public static RealtimeEvent Create(string eventName, object data)
        => new() { Event = eventName, Data = data };
}