package com.project.workspace_service.domain.aggregate.channel;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.domain.enums.ChannelRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelMember {
    private final UUID id;
    private final UUID channelId;
    private final UUID userId;
    private final ChannelRole role; // ADMIN, MEMBER
    private final LocalDateTime joinedAt;
    private final LocalDateTime leftAt;
}