package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceMemberJpaEntity;

@Repository
public interface WorkspaceMemberJpaRepository extends JpaRepository<WorkspaceMemberJpaEntity, UUID> {
    // Tìm member trong 1 workspace cụ thể (VD: để check quyền)
    Optional<WorkspaceMemberJpaEntity> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

}