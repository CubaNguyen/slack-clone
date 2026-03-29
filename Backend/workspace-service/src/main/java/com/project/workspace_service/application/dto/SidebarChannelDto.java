package com.project.workspace_service.application.dto;

import java.util.UUID;

public record SidebarChannelDto(
                UUID id,
                String name,
                String type, // "PUBLIC" hoặc "PRIVATE"
                boolean isDefault, // Để Frontend chặn nút "Leave" ở kênh mặc định
                boolean isArchived // Để xử lý hiển thị (làm mờ/ẩn)
) {
}