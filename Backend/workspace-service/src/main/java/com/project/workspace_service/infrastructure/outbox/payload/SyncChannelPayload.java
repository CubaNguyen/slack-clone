package com.project.workspace_service.infrastructure.outbox.payload;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncChannelPayload {
    private UUID id;
    private UUID workspaceId;
    private String name;
    private String type;
    private boolean isArchived;
}