package com.project.workspace_service.application.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationValidationResponse {
    private boolean isValid;
    private String email; // Để frontend điền sẵn vào ô đăng ký/đăng nhập
    private String workspaceName; // Để hiện: "Mời bạn vào [Workspace Name]"
    private UUID workspaceId;
    private String role; // ADMIN/MEMBER

    private boolean isUserExist;
}