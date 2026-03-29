package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelJpaEntity;

import java.util.UUID;

@Repository
public interface ChannelJpaRepository extends JpaRepository<ChannelJpaEntity, UUID> {
    // Check trùng tên channel trong cùng 1 workspace
    boolean existsByWorkspaceIdAndName(UUID workspaceId, String name);
}