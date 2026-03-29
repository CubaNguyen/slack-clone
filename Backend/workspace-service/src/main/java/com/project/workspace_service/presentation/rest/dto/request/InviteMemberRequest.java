package com.project.workspace_service.presentation.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InviteMemberRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Schema(description = "Email người được mời", example = "dev@example.com")
    private String email;

    @NotBlank(message = "Role không được để trống")
    @Pattern(regexp = "^(MEMBER|ADMIN/GUEST)$", message = "Role chỉ được là MEMBER,GUEST hoặc ADMIN")
    @Schema(description = "Quyền hạn", example = "MEMBER")
    private String role;
}