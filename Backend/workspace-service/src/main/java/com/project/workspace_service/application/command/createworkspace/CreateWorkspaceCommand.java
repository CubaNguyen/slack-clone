package com.project.workspace_service.application.command.createworkspace;

import java.util.UUID;

// DTO đơn giản chứa thông tin cần thiết để tạo
public record CreateWorkspaceCommand(
        String name,
        String slug,
        UUID ownerId // ID của user đang login (lấy từ token)
) {
}