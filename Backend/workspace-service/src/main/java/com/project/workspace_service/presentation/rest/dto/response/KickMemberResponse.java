package com.project.workspace_service.presentation.rest.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KickMemberResponse {
    private UUID kickedMemberId; // Trả lại ID người vừa bị kick để FE xóa dòng tương ứng trong bảng
    private String message;
}