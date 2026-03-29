package com.project.workspace_service.application.dto;

import java.util.UUID;

public record ChannelDto(
        UUID id,
        String name,
        String type,
        boolean isDefault,
        boolean isArchived,
        UUID workspaceId) {
}
