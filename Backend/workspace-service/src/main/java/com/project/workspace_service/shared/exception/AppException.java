// package com.project.workspace_service.shared.exception;

// import lombok.Getter;
// import org.springframework.http.HttpStatus;

// @Getter
// public class AppException extends RuntimeException {

//     private final HttpStatus status;
//     private final String code; // Mã lỗi dạng string (VD: USER_NOT_FOUND)

//     // Cách 1: Chỉ cần truyền message (Mặc định 400 Bad Request)
//     public AppException(String message) {
//         super(message);
//         this.status = HttpStatus.BAD_REQUEST;
//         this.code = "API_ERROR"; // Code mặc định nếu lười truyền
//     }

//     // Cách 2: Truyền message + status (VD: 404 Not Found)
//     public AppException(String message, HttpStatus status) {
//         super(message);
//         this.status = status;
//         this.code = status.name(); // Lấy luôn tên status làm code (VD: NOT_FOUND)
//     }

//     // Cách 3: Truyền đầy đủ (nếu muốn code xịn xò như Identity)
//     public AppException(String message, String code, HttpStatus status) {
//         super(message);
//         this.code = code;
//         this.status = status;
//     }
// }

package com.project.workspace_service.shared.exception;

import com.project.workspace_service.shared.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    // Constructor chuẩn: Truyền Enum vào
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // (Optional) Constructor nâng cao:
    // Dành cho lúc muốn giữ nguyên Code/Status nhưng muốn thay đổi Message chi tiết
    // hơn
    // Ví dụ: "Slug này đã được sử dụng: my-workspace"
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}