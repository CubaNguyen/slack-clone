package com.project.workspace_service.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua field null (để data null thì không hiện, error null không hiện)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetail error;

    // Class con định nghĩa chi tiết lỗi
    @Data
    @Builder
    public static class ErrorDetail {
        private String code;
        private String path;
        private LocalDateTime timestamp;
    }

    // --- Helper Method: Trả về thành công ---
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .error(null)
                .build();
    }

    // --- Helper Method: Trả về lỗi ---
    public static <T> ApiResponse<T> error(String message, String code, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .error(ErrorDetail.builder()
                        .code(code)
                        .path(path)
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }
}