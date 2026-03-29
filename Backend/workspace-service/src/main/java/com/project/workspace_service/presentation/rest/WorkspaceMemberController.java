package com.project.workspace_service.presentation.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workspace_service.application.command.kickmember.KickMemberCommand;
import com.project.workspace_service.application.command.kickmember.KickMemberHandler;
import com.project.workspace_service.presentation.rest.dto.response.KickMemberResponse; // DTO trả về
import com.project.workspace_service.shared.response.ApiResponse;
import com.project.workspace_service.shared.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workspaces/{workspaceId}/members") // URL chuẩn RESTful
@RequiredArgsConstructor
@Tag(name = "Workspace Members", description = "Quản lý thành viên trong Workspace")
public class WorkspaceMemberController {

    private final KickMemberHandler kickMemberHandler;

    // URL: DELETE /api/v1/workspaces/{workspaceId}/members/{memberId}
    @DeleteMapping("/{memberId}")
    @Operation(summary = "Xóa thành viên (Kick)", description = "Admin/Owner xóa thành viên khỏi workspace")
    public ResponseEntity<ApiResponse<KickMemberResponse>> kickMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId) { // Đổi tên biến thành memberId cho đỡ nhầm với currentUserId

        // 1. [TỰ ĐỘNG] Lấy ID của Admin/Owner đang thực hiện thao tác
        UUID currentAdminId = SecurityUtils.getCurrentUserId();

        // 2. Tạo Command
        KickMemberCommand command = new KickMemberCommand(workspaceId, currentAdminId, memberId);

        // 3. Gọi Handler
        kickMemberHandler.handle(command);

        // 4. Tạo DTO trả về (Cho Frontend dễ xử lý)
        KickMemberResponse response = KickMemberResponse.builder()
                .kickedMemberId(memberId)
                .message("Đã xóa thành viên khỏi workspace thành công")
                .build();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Xóa thành viên thành công"));
    }
}