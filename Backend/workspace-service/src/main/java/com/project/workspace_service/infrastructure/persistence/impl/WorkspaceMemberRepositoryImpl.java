package com.project.workspace_service.infrastructure.persistence.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.project.workspace_service.domain.aggregate.workspace.WorkspaceMember;
import com.project.workspace_service.domain.repository.WorkspaceMemberRepository;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceMemberJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.repository.WorkspaceMemberJpaRepository;
import com.project.workspace_service.infrastructure.persistence.mapper.WorkspaceMemberMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceMemberRepositoryImpl implements WorkspaceMemberRepository {
    private final WorkspaceMemberJpaRepository jpaRepository;
    private final WorkspaceMemberMapper mapper; // Inject mapper vào đây

    @Override
    public void save(WorkspaceMember member) {
        WorkspaceMemberJpaEntity entity = mapper.toEntity(member);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return jpaRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .map(mapper::toDomain);
    }
}