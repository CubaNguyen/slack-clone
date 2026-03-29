package com.project.workspace_service.presentation.rest.dto.request;

import com.project.workspace_service.domain.enums.ChannelType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateChannelRequest {

    @NotBlank(message = "Tên channel không được để trống")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Tên channel chỉ được chứa chữ thường, số và gạch ngang (kebab-case)")
    @Schema(description = "Tên channel", example = "team-backend")
    private String name;

    @NotNull(message = "Loại channel không được để trống")
    @Schema(description = "Loại channel (PUBLIC/PRIVATE)", example = "PUBLIC")
    private ChannelType type;

}