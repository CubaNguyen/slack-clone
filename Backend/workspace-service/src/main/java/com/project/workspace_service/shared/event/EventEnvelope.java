package com.project.workspace_service.shared.event;

import java.time.LocalDateTime;
import java.util.UUID;

// Class bọc bên ngoài cùng
public record EventEnvelope(Meta meta, Object data) {

    // Phần Meta cố định
    public record Meta(
            UUID event_id,
            String trace_id,
            Actor actor,
            LocalDateTime occurred_at) {
    }

    // Thông tin người thực hiện
    public record Actor(
            UUID user_id,
            String role) {
    }
}