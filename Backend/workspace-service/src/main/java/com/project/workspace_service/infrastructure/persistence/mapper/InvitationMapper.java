package com.project.workspace_service.infrastructure.persistence.mapper;

import com.project.workspace_service.domain.aggregate.invitation.Invitation;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.InvitationJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceJpaEntity;

public class InvitationMapper {

    // Chuyển từ Domain sang JPA Entity để lưu vào DB
    // Trong InvitationMapper.java
    public static InvitationJpaEntity toJpa(Invitation domain) {
        if (domain == null)
            return null;

        return InvitationJpaEntity.builder()
                .id(domain.getId())
                // Vì JPA Entity cần object Workspace, ta tạo một Proxy entity chỉ có ID
                .workspace(WorkspaceJpaEntity.builder().id(domain.getWorkspaceId()).build())
                .inviterId(domain.getInviterId())
                .inviterEmail(domain.getInviterEmail())
                .email(domain.getEmail())
                .role(domain.getRole())
                .status(domain.getStatus())
                .acceptedAt(domain.getAcceptedAt())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    // Chuyển ngược từ JPA Entity sang Domain (Nếu cần dùng trong RepositoryImpl)
    public static Invitation toDomain(InvitationJpaEntity entity) {
        if (entity == null)
            return null;

        return Invitation.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .workspaceId(entity.getWorkspace() != null ? entity.getWorkspace().getId() : null)
                .inviterId(entity.getInviterId())
                .inviterEmail(entity.getInviterEmail())
                .status(entity.getStatus())
                .acceptedAt(entity.getAcceptedAt())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}