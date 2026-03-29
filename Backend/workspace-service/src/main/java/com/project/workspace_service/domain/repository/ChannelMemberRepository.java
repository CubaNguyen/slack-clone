package com.project.workspace_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.project.workspace_service.domain.aggregate.channel.ChannelMember;

public interface ChannelMemberRepository {
    void save(ChannelMember member);

    void deleteByChannelIdAndUserId(UUID channelId, UUID userId);

    void deleteAllByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<ChannelMember> findByChannelIdAndUserId(UUID channelId, UUID userId);
}