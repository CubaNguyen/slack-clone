package com.project.workspace_service.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.domain.enums.WorkspaceRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workspace_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "workspace_id", "user_id" })
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceJpaEntity workspace;

    @Column(name = "user_id", nullable = false)
    private UUID userId; // Từ Identity Service

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceRole role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public static WorkspaceMemberJpaEntity createOwner(UUID workspaceId, UUID userId) {
        WorkspaceMemberJpaEntity entity = new WorkspaceMemberJpaEntity();
        entity.setId(UUID.randomUUID());

        // Tạo object rỗng chỉ chứa ID để Hibernate link khóa ngoại (không cần query DB)
        WorkspaceJpaEntity ws = new WorkspaceJpaEntity();
        ws.setId(workspaceId);
        entity.setWorkspace(ws);

        entity.setUserId(userId);
        entity.setRole(WorkspaceRole.OWNER); // Set quyền cao nhất
        entity.setJoinedAt(LocalDateTime.now());

        return entity;
    }
}