package com.project.workspace_service.domain.event;

import com.project.workspace_service.shared.DomainEvent; // 👈 Nhớ import dòng này
import lombok.Getter;

import java.util.UUID;

@Getter
public class WorkspaceCreatedEvent extends DomainEvent {

    private final UUID workspaceId;
    private final UUID ownerId;

    public WorkspaceCreatedEvent(UUID workspaceId, UUID ownerId) {
        super(); // Gọi cha để sinh eventId và occurredOn tự động
        this.workspaceId = workspaceId;
        this.ownerId = ownerId;
    }

    // --- Implement logic cho Outbox ---

    @Override
    public UUID getAggregateId() {
        return this.workspaceId; // Event này thuộc về Workspace này
    }

    @Override
    public String getAggregateType() {
        return "WORKSPACE"; // Định danh aggregate
    }
}