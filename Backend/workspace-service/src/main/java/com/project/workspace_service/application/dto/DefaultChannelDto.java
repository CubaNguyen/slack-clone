package com.project.workspace_service.application.dto;

import java.util.UUID;

// Vị trí: application/dto/DefaultChannelDto.java
public record DefaultChannelDto(
                UUID id,
                String name,
                boolean isDefault) {
}