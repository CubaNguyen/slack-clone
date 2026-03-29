package com.project.workspace_service.application.dto;

import java.util.UUID;

public record BrowseChannelDto(
        UUID id,
        String name,
        boolean isJoined, // Để biết User đã tham gia chưa
        int memberCount // (Optional) Cho user biết kênh này đông vui không
) {
}