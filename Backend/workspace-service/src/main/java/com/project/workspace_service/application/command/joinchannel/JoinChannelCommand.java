package com.project.workspace_service.application.command.joinchannel;

import java.util.UUID;

public record JoinChannelCommand(
        UUID workspaceId,
        UUID channelId,
        UUID userId) {
}