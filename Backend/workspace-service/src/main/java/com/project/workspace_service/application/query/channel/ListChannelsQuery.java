package com.project.workspace_service.application.query.channel;

import java.util.UUID;

public record ListChannelsQuery(UUID workspaceId, UUID userId) {
}