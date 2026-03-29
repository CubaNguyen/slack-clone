package com.project.workspace_service.domain.repository;

import java.util.UUID;

import com.project.workspace_service.domain.aggregate.channel.Channel;

public interface ChannelRepository {
    void save(Channel channel);

    boolean existsByWorkspaceIdAndName(UUID workspaceId, String name);
}
