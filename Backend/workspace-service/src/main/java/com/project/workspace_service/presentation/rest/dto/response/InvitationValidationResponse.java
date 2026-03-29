package com.project.workspace_service.presentation.rest.dto.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvitationValidationResponse {

    @Schema(description = "Token có hợp lệ không", example = "true")
    private boolean isValid;

    @Schema(description = "Email người được mời", example = "user@example.com")
    private String email;

    @Schema(description = "Tên workspace mời", example = "K14 Team")
    private String workspaceName;

    private UUID workspaceId;

    @Schema(description = "Vai trò được mời", example = "MEMBER")
    private String role;
    @Schema(description = "Người dùng đã tồn tại trong hệ thống chưa", example = "false")
    private boolean isUserExist;
}