package com.project.workspace_service.application.command.acceptinvitation;

import java.util.UUID;

public record AcceptInvitationCommand(
        String token,
        UUID currentUserId, // Lấy từ JWT
        String currentUserEmail // Lấy từ JWT (Quan trọng để check khớp mail)
) {
}