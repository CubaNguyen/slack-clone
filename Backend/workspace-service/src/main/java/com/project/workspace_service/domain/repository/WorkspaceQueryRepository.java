package com.project.workspace_service.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.workspace_service.application.dto.WorkspaceDto;

public interface WorkspaceQueryRepository {
    List<WorkspaceDto> findAllByUserId(UUID userId);

    boolean isUserInWorkspace(UUID workspaceId, UUID userId);

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<String> getNameById(UUID id);
}