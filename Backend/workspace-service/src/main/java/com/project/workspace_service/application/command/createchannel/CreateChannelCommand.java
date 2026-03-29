package com.project.workspace_service.application.command.createchannel;

import java.util.UUID;

import com.project.workspace_service.domain.enums.ChannelType;

public record CreateChannelCommand(
                UUID workspaceId,
                UUID userId,
                String name,
                ChannelType type) {
}