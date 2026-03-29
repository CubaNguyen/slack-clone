package com.project.workspace_service.presentation.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptInvitationRequest {
    @NotBlank(message = "Token không được để trống")
    @Schema(description = "Token nhận được từ email mời", example = "abc-123-xyz")
    private String token;
}