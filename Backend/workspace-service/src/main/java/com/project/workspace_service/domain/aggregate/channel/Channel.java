package com.project.workspace_service.domain.aggregate.channel;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.domain.enums.ChannelType;
import com.project.workspace_service.domain.event.ChannelCreatedEvent;
import com.project.workspace_service.shared.AggregateRoot;

import lombok.Getter;

@Getter
public class Channel extends AggregateRoot {
    private UUID id;
    private UUID workspaceId;
    private String name;
    private ChannelType type;
    private UUID createdBy;
    private LocalDateTime createdAt;

    private Channel(UUID id, UUID workspaceId, String name, ChannelType type, UUID createdBy) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.name = name;
        this.type = type;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now(); // [2] QUAN TRỌNG: Khởi tạo thời gian ngay khi new
    }

    public static Channel create(UUID workspaceId, String name, ChannelType type, UUID creatorId) {
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(channelId, workspaceId, name, type, creatorId);

        // Bắn event để Saga tự động add member sau này [Suy luận]
        channel.addDomainEvent(new ChannelCreatedEvent(channelId, workspaceId, creatorId, name, type));
        return channel;
    }
}