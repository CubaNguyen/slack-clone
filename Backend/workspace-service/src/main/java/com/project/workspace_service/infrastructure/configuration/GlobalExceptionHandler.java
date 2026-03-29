package com.project.workspace_service.infrastructure.configuration;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Nhớ import cái này
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;
import com.project.workspace_service.shared.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // 1. Xử lý lỗi do chính bạn throw ra (AppException)
        @ExceptionHandler(AppException.class)
        public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
                ErrorCode errorCode = ex.getErrorCode();

                ApiResponse<Object> response = ApiResponse.error(
                                ex.getMessage(), // Lấy message (có thể là custom hoặc default)
                                errorCode.getCode(), // Lấy code (VD: WS_003)
                                null // path
                );

                return ResponseEntity
                                .status(errorCode.getStatusCode()) // Lấy status (VD: 409 Conflict)
                                .body(response);
        }

        // 2. Xử lý các lỗi không lường trước (Bug, NullPointer...) -> Trả về 500
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, HttpServletRequest request) {
                ApiResponse<Object> response = ApiResponse.error(
                                "Internal Server Error: " + ex.getMessage(), // Hoặc giấu message lỗi đi nếu muốn bảo
                                                                             // mật
                                "INTERNAL_SERVER_ERROR",
                                request.getRequestURI());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // --- BỔ SUNG: Xử lý lỗi Validation (@NotBlank, @Size...) ---
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                // Lấy message lỗi đầu tiên (VD: "Tên workspace không được để trống")
                String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();

                ApiResponse<Object> response = ApiResponse.error(
                                errorMessage,
                                "VALIDATION_ERROR", // Hoặc mã lỗi bạn tự quy định (400)
                                request.getRequestURI());

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

}
