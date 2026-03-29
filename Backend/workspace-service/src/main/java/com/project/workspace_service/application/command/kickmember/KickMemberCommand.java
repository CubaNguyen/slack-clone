package com.project.workspace_service.application.command.kickmember;

import java.util.UUID;

public record KickMemberCommand(
        UUID workspaceId,
        UUID adminId, // Người thực hiện kick
        UUID targetUserId // Người bị kick
) {
}