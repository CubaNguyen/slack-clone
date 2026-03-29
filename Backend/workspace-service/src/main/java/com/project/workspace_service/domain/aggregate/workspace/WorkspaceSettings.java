package com.project.workspace_service.domain.aggregate.workspace;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Ép buộc dùng Factory method
public class WorkspaceSettings {

    private final UUID workspaceId;
    private boolean allowMemberCreateChannel;
    private boolean allowMemberArchiveChannel;
    private boolean allowMemberInviteGuest;
    private int messageRetentionDays;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    // [Logic] Factory method để tạo bộ cài đặt mặc định khi tạo mới Workspace
    public static WorkspaceSettings createDefault(UUID workspaceId) {
        return new WorkspaceSettings(
                workspaceId,
                true, // allowMemberCreateChannel: Mặc định cho phép nhân viên tạo kênh [Suy luận]
                false, // allowMemberArchiveChannel: Chặn nhân viên lưu trữ kênh (tránh mất data) [Suy
                       // luận]
                false, // allowMemberInviteGuest: Bảo mật, không cho mời khách tự do [Suy luận]
                0, // messageRetentionDays: 0 là lưu vĩnh viễn [Suy luận]
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    public static WorkspaceSettings restore(UUID workspaceId, boolean createChannel, boolean archiveChannel,
            boolean inviteGuest, int retentionDays, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new WorkspaceSettings(workspaceId, createChannel, archiveChannel, inviteGuest, retentionDays, createdAt,
                updatedAt);
    }

    // [Hành vi] Cập nhật cài đặt (Chỉ những ai có quyền mới được gọi hàm này)
    // Trong WorkspaceSettings.java
    public void update(boolean createChannel, boolean archiveChannel, boolean inviteGuest, int retentionDays) {
        // Thêm logic validate nếu cần, ví dụ retentionDays không được âm
        if (retentionDays < 0)
            throw new AppException(ErrorCode.INVALID_RETENTION_DAYS); // Sạch sẽ hơn nhiều
        this.allowMemberCreateChannel = createChannel;
        this.allowMemberArchiveChannel = archiveChannel;
        this.allowMemberInviteGuest = inviteGuest;
        this.messageRetentionDays = retentionDays;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}