package com.project.workspace_service.domain.event;

import java.util.UUID;

import com.project.workspace_service.domain.enums.ChannelType; // <--- Nhớ Import Enum của ông
import com.project.workspace_service.shared.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor; // Cần thiết cho Jackson nếu sau này deserialize
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(force = true) // Jackson cần cái này
public class ChannelCreatedEvent extends DomainEvent {
    private final UUID channelId;
    private final UUID workspaceId;
    private final UUID creatorId;
    private final String name;
    private final String type; // Lưu String để gửi JSON cho dễ

    // Constructor nhận Enum (ChannelType) để khớp với code trong Handler
    public ChannelCreatedEvent(UUID channelId, UUID workspaceId, UUID creatorId, String name, ChannelType typeEnum) {
        super();
        this.channelId = channelId;
        this.workspaceId = workspaceId;
        this.creatorId = creatorId;
        this.name = name;

        // CỐT LÕI NẰM Ở ĐÂY:
        // Chuyển Enum thành String ("PUBLIC", "PRIVATE") ngay khi tạo Event
        this.type = typeEnum.name();
    }

    @Override
    public UUID getAggregateId() {
        return this.channelId;
    }

    @Override
    public String getAggregateType() {
        return "CHANNEL";
    }
}