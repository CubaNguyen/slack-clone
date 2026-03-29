package com.project.workspace_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.project.workspace_service.domain.aggregate.workspace.WorkspaceMember;

public interface WorkspaceMemberRepository {
    void save(WorkspaceMember member);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}