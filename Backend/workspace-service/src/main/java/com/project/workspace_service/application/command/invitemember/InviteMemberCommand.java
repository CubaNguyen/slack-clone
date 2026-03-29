package com.project.workspace_service.application.command.invitemember;

import java.util.UUID;

public record InviteMemberCommand(
                UUID workspaceId,
                UUID inviterId,
                String inviterEmail,
                String email,
                String role) {
}