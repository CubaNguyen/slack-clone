package com.project.workspace_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.project.workspace_service.domain.aggregate.workspace.Workspace;

public interface WorkspaceRepository {
    // 1. Hàm lưu (Nhận vào Domain Model)
    void save(Workspace workspace);

    // 2. Hàm check slug
    boolean existsBySlug(String slug);

    Optional<Workspace> findById(UUID id);

    // 4. [BỔ SUNG] Lấy Role của User trong Workspace (Để Handler check quyền
    // Member/Admin)
    String getUserRole(UUID workspaceId, UUID userId);
}