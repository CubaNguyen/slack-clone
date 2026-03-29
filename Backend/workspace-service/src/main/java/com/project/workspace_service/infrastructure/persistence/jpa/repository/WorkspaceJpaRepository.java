package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceJpaEntity;

@Repository
public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceJpaEntity, UUID> {
    // Để check trùng slug khi tạo
    boolean existsBySlug(String slug);

}