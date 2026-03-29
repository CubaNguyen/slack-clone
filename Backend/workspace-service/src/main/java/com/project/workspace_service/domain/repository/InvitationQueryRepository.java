package com.project.workspace_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.project.workspace_service.domain.aggregate.invitation.Invitation;

public interface InvitationQueryRepository {
    // Dùng JDBC để check nhanh sự tồn tại
    boolean existsByEmailAndWorkspaceIdAndStatus(String email, UUID workspaceId, String status);

    Optional<Invitation> findByToken(String token);
}