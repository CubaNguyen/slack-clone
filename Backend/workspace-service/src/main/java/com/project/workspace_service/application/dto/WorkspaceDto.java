package com.project.workspace_service.application.dto;

import java.util.UUID;

public record WorkspaceDto(
        UUID id,
        String name,
        String slug,
        String role, // Trả về luôn role của user trong WS đó (OWNER/MEMBER)
        long memberCount // Ví dụ sau này muốn hiển thị số lượng tv
) {
}