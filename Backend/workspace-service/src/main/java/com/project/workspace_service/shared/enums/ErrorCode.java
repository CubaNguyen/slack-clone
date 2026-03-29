package com.project.workspace_service.shared.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
        // === 1. LỖI HỆ THỐNG & CHUNG (Prefix: SYS) ===
        UNCATEGORIZED_EXCEPTION("SYS_999", "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
        EMAIL_SEND_FAILED("SYS_001", "Hệ thống gặp sự cố khi gửi email. Vui lòng thử lại sau.",
                        HttpStatus.INTERNAL_SERVER_ERROR),
        USER_SERVICE_UNAVAILABLE("SYS_002", "Dịch vụ xác thực người dùng hiện không khả dụng.",
                        HttpStatus.SERVICE_UNAVAILABLE),

        // === 2. LỖI AUTH & USER (Prefix: AUTH/USER) ===
        UNAUTHENTICATED("AUTH_001", "Vui lòng đăng nhập để tiếp tục.", HttpStatus.UNAUTHORIZED),
        UNAUTHORIZED_ACCESS("AUTH_002", "Thông tin người dùng không hợp lệ (Missing ID).", HttpStatus.UNAUTHORIZED),
        USER_NOT_FOUND("USER_001", "Người dùng không tồn tại trong hệ thống.", HttpStatus.NOT_FOUND),

        // === 3. LỖI WORKSPACE (Prefix: WS) ===
        WORKSPACE_NOT_FOUND("WS_001", "Workspace không tồn tại.", HttpStatus.NOT_FOUND),
        WORKSPACE_EXISTED("WS_002", "Workspace đã tồn tại.", HttpStatus.BAD_REQUEST),
        SLUG_ALREADY_EXISTS("WS_003", "Slug này đã được sử dụng.", HttpStatus.CONFLICT),
        INVALID_RETENTION_DAYS("WS_004", "Ngày lưu trữ không được là số âm.", HttpStatus.BAD_REQUEST),
        NOT_IN_WORKSPACE("WS_006", "Bạn không phải thành viên của Workspace này.", HttpStatus.FORBIDDEN),
        USER_ALREADY_IN_WORKSPACE("WS_007", "Người dùng này đã là thành viên của Workspace.", HttpStatus.BAD_REQUEST),
        CANNOT_KICK_SELF("WS_008", "Bạn không thể tự trục xuất chính mình.", HttpStatus.BAD_REQUEST),
        CANNOT_KICK_OWNER("WS_009", "Không thể trục xuất chủ sở hữu (Owner) của Workspace.", HttpStatus.FORBIDDEN),
        NO_PERMISSION_TO_KICK("WS_010", "Bạn không có quyền thực hiện hành động này.", HttpStatus.FORBIDDEN),
        CANNOT_KICK_YOURSELF("WS_011", "Bạn không thể tự trục xuất chính mình, hãy dùng chức năng 'Rời Workspace'.",
                        HttpStatus.BAD_REQUEST),
        // === 4. LỖI CHANNEL (Prefix: CH) ===
        CHANNEL_NOT_FOUND("CH_001", "Kênh không tồn tại.", HttpStatus.NOT_FOUND),
        CHANNEL_ALREADY_EXISTS("CH_002", "Tên kênh đã tồn tại trong workspace này.", HttpStatus.BAD_REQUEST),
        ALREADY_MEMBER("CH_003", "Bạn đã là thành viên của kênh này.", HttpStatus.BAD_REQUEST),
        USER_NOT_IN_CHANNEL("CH_004", "Bạn không phải thành viên của kênh này.", HttpStatus.NOT_FOUND),
        PRIVATE_CHANNEL_ACCESS_DENIED("CH_005", "Không thể tự tham gia kênh riêng tư.", HttpStatus.FORBIDDEN),
        CANNOT_LEAVE_DEFAULT_CHANNEL("CH_006", "Bạn không thể rời kênh mặc định.", HttpStatus.BAD_REQUEST),
        CANNOT_LEAVE_LAST_ADMIN("CH_007", "Bạn là Admin cuối cùng. Vui lòng chuyển quyền trước khi rời.",
                        HttpStatus.BAD_REQUEST),

        // === 5. LỖI INVITATION (Prefix: INV) ===
        INVITATION_NOT_FOUND("INV_001", "Lời mời không tồn tại hoặc đã bị thu hồi.", HttpStatus.NOT_FOUND),
        INVITATION_ALREADY_SENT("INV_002", "Đã gửi lời mời cho email này rồi.", HttpStatus.BAD_REQUEST),
        CANNOT_INVITE_SELF("INV_003", "Bạn không thể gửi lời mời cho chính mình.", HttpStatus.BAD_REQUEST),
        INVALID_ROLE("INV_004", "Vai trò không hợp lệ.", HttpStatus.BAD_REQUEST),
        INVITATION_ALREADY_USED("INV_005", "Lời mời này đã được sử dụng hoặc không còn hiệu lực.",
                        HttpStatus.BAD_REQUEST),
        INVITATION_EXPIRED("INV_006", "Lời mời đã hết hạn sử dụng (quá 7 ngày).", HttpStatus.GONE),
        INVITATION_EMAIL_MISMATCH("INV_007", "Email đăng nhập không khớp với email được mời.", HttpStatus.FORBIDDEN);

        private final String code;
        private final String message;
        private final HttpStatus statusCode;

        ErrorCode(String code, String message, HttpStatus statusCode) {
                this.code = code;
                this.message = message;
                this.statusCode = statusCode;
        }
}