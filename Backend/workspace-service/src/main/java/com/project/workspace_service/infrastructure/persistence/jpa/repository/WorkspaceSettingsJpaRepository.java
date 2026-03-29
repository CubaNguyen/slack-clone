package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceSettingsJpaEntity;

import java.util.UUID;

@Repository
public interface WorkspaceSettingsJpaRepository extends JpaRepository<WorkspaceSettingsJpaEntity, UUID> {
}