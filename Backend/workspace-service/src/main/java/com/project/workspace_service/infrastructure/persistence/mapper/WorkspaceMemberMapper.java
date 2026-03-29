package com.project.workspace_service.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.project.workspace_service.domain.aggregate.workspace.WorkspaceMember;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceMemberJpaEntity;

@Component
public class WorkspaceMemberMapper {

    // Từ Domain sang JPA Entity (Dùng cho Save)
    public WorkspaceMemberJpaEntity toEntity(WorkspaceMember domain) {
        if (domain == null)
            return null;

        WorkspaceJpaEntity workspaceRef = new WorkspaceJpaEntity();
        workspaceRef.setId(domain.getWorkspaceId()); // Dùng Proxy Object để tối ưu

        return WorkspaceMemberJpaEntity.builder()
                .id(domain.getId())
                .workspace(workspaceRef)
                .userId(domain.getUserId())
                .role(domain.getRole()) // Entity nhận Enum WorkspaceRole
                .joinedAt(domain.getJoinedAt())
                .leftAt(domain.getLeftAt())
                .build();
    }

    // Từ JPA Entity sang Domain (Dùng cho Find)
    public WorkspaceMember toDomain(WorkspaceMemberJpaEntity entity) {
        if (entity == null)
            return null;

        return WorkspaceMember.builder()
                .id(entity.getId())
                .workspaceId(entity.getWorkspace().getId()) // Lấy ID từ quan hệ ManyToOne
                .userId(entity.getUserId())
                .role(entity.getRole())
                .joinedAt(entity.getJoinedAt())
                .leftAt(entity.getLeftAt())
                .build();
    }
}