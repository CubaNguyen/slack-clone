package com.project.workspace_service.infrastructure.persistence.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.domain.aggregate.workspace.Workspace;
import com.project.workspace_service.domain.repository.WorkspaceRepository;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.repository.WorkspaceJpaRepository;
import com.project.workspace_service.infrastructure.persistence.mapper.WorkspaceMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepositoryImpl implements WorkspaceRepository {

    private final WorkspaceJpaRepository jpaRepository; // Cái có sẵn của Spring Data JPA
    private final WorkspaceMapper workspaceMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsBySlug(String slug) {
        // Gọi thằng JPA làm dùm
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public void save(Workspace workspace) {

        WorkspaceJpaEntity entitys = workspaceMapper.toEntity(workspace);
        // Map thêm các field khác nếu có...

        jpaRepository.save(entitys);
    }

    @Override
    public Optional<Workspace> findById(UUID id) {
        // Tìm JPA Entity -> Convert ngược về Domain Entity
        return jpaRepository.findById(id)
                .map(WorkspaceMapper::toDomain); // Ông nhớ viết hàm toDomain trong Mapper nhé
    }

    @Override
    public String getUserRole(UUID workspaceId, UUID userId) {
        // Dùng SQL thuần query thẳng vào bảng workspace_members cho nhanh gọn
        String sql = "SELECT role FROM workspace_members WHERE workspace_id = ? AND user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, workspaceId, userId);
        } catch (Exception e) {
            return null; // Không tìm thấy hoặc lỗi thì trả về null (User chưa join)
        }
    }
}