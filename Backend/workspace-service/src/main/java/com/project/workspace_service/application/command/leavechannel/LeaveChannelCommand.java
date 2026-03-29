package com.project.workspace_service.application.command.leavechannel;

import java.util.UUID;

public record LeaveChannelCommand(
        UUID workspaceId,
        UUID channelId,
        UUID userId) {
}