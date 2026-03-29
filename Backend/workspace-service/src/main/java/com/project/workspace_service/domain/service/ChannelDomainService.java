package com.project.workspace_service.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.workspace_service.domain.aggregate.channel.Channel;
import com.project.workspace_service.domain.aggregate.workspace.Workspace;
import com.project.workspace_service.domain.enums.ChannelType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelDomainService {

    // Logic nghiệp vụ cốt lõi [Suy luận]
    public Channel createChannel(Workspace workspace, String name, ChannelType type, UUID creatorId,
            String creatorRole) {

        // 1. Kiểm tra quyền tạo kênh dựa trên Settings
        // Nếu không phải ADMIN/OWNER và settings chặn member tạo kênh thì throw lỗi
        if (!isAuthorizedToCreate(workspace, creatorRole)) {
            throw new RuntimeException("Bạn không có quyền tạo kênh trong Workspace này.");
        }

        // 2. Khởi tạo đối tượng Channel (Aggregate mới)
        return Channel.create(workspace.getId(), name, type, creatorId);
    }

    private boolean isAuthorizedToCreate(Workspace workspace, String role) {
        if ("OWNER".equals(role) || "ADMIN".equals(role)) {
            return true;
        }
        // Check vào cái bảng Settings mà ông vừa cực khổ fix xong nè [Suy luận]
        return workspace.getSettings().isAllowMemberCreateChannel();
    }
}