package com.project.workspace_service.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.workspace_service.application.dto.BrowseChannelDto;
import com.project.workspace_service.application.dto.ChannelDto;
import com.project.workspace_service.application.dto.DefaultChannelDto;
import com.project.workspace_service.application.dto.SidebarChannelDto;

public interface ChannelQueryRepository {
    List<SidebarChannelDto> findMySidebarChannels(UUID workspaceId, UUID userId);

    List<BrowseChannelDto> findAllPublicChannels(UUID workspaceId, UUID userId);

    Optional<ChannelDto> findById(UUID id);

    List<DefaultChannelDto> findDefaultChannelsByWorkspaceId(UUID workspaceId);

}