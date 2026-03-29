namespace ChatService.Application.Common.Constants;

public static class EventTypes
{
    // Real-time (Redis -> Go -> Web)
    public static class Realtime
    {
        public const string MessageCreated = "MESSAGE_CREATED";
        public const string MessageDeleted = "MESSAGE_DELETED";
        public const string MessageUpdated = "MESSAGE_UPDATED";
        public const string MessagePinToggled = "MESSAGE_PIN_TOGGLED";
        public const string ReactionUpdated = "REACTION_UPDATED";
        public const string UserTyping = "USER_TYPING"; // Dự phòng cho tương lai nè
    }

    // Background Tasks (Kafka -> Worker -> DB)
    public static class Background
    {
        public const string MessageReplied = "message-reply-events";
        public const string ChannelRead = "channel-read-events";
    }
}