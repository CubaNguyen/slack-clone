package com.project.workspace_service.domain.repository;

import java.util.UUID;

public interface ChannelMemberQueryRepository {
    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);

    long countAdminsByChannelId(UUID channelId);
}
