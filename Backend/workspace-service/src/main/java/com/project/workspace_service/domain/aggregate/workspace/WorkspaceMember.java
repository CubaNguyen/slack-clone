package com.project.workspace_service.domain.aggregate.workspace;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.domain.enums.WorkspaceRole;
import com.project.workspace_service.shared.AggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WorkspaceMember extends AggregateRoot {
    private final UUID id;
    private final UUID workspaceId; // Ở Domain chỉ cần ID, không cần cả Object Entity
    private final UUID userId;
    private WorkspaceRole role;
    private final LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    // Factory method để tạo member mới từ lời mời
    public static WorkspaceMember create(UUID workspaceId, UUID userId, WorkspaceRole role) {
        return WorkspaceMember.builder()
                .id(UUID.randomUUID())
                .workspaceId(workspaceId)
                .userId(userId)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    // Logic nghiệp vụ: Rời khỏi workspace
    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

}