package com.project.workspace_service.shared.utils;

import java.util.UUID;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return null;

        HttpServletRequest request = attributes.getRequest();

        // Luôn đọc từ Header (Chuẩn Gateway)
        String userIdStr = request.getHeader("x-user-id");
        System.out.println("Debug: User ID from header = " + userIdStr);
        if (userIdStr == null || userIdStr.isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "User ID is missing in header");
        }

        return UUID.fromString(userIdStr);
    }

    public static String getCurrentUserEmail() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return null;

        // Đọc từ Header "x-user-email"
        String email = attributes.getRequest().getHeader("x-user-email");

        if (email == null || email.isBlank()) {
            // Nếu chạy Local mà chưa qua Filter -> Lỗi
            // Nếu chạy Prod mà Gateway quên gửi -> Lỗi
            throw new AppException(ErrorCode.UNAUTHENTICATED, "User Email is missing in header");
        }

        return email;
    }
}