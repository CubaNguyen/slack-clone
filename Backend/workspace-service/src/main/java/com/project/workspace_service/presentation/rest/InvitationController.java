package com.project.workspace_service.presentation.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// 👇 Import RequestBody của Spring
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.workspace_service.application.command.acceptinvitation.AcceptInvitationCommand;
import com.project.workspace_service.application.command.acceptinvitation.AcceptInvitationHandler;
import com.project.workspace_service.application.command.invitemember.InviteMemberCommand;
import com.project.workspace_service.application.command.invitemember.InviteMemberHandler;
import com.project.workspace_service.application.query.invitation.validateInvitation.ValidateInvitationHandler;
import com.project.workspace_service.presentation.rest.dto.request.AcceptInvitationRequest;
import com.project.workspace_service.presentation.rest.dto.request.InviteMemberRequest;
import com.project.workspace_service.presentation.rest.dto.response.AcceptInvitationResponse;
import com.project.workspace_service.presentation.rest.dto.response.InvitationValidationResponse;
import com.project.workspace_service.shared.response.ApiResponse;
import com.project.workspace_service.shared.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invitations") // 👈 Root path là tên Resource
@RequiredArgsConstructor
@Tag(name = "Invitation", description = "Các API quản lý lời mời tham gia Workspace")
public class InvitationController {

        private final InviteMemberHandler inviteMemberHandler;
        private final ValidateInvitationHandler validateInvitationHandler;
        private final AcceptInvitationHandler acceptInvitationHandler;

        // 👇 URL đầy đủ: POST /api/v1/invitations/workspace/{workspaceId}
        @PostMapping("/workspace/{workspaceId}")
        @Operation(summary = "Mời thành viên", description = "Gửi email mời thành viên tham gia workspace")
        public ResponseEntity<ApiResponse<Void>> inviteMember(
                        @PathVariable UUID workspaceId,
                        @RequestBody @Valid InviteMemberRequest request) {

                // 1. [TỰ ĐỘNG] Lấy ID người mời (Admin/Owner)
                UUID currentUserId = SecurityUtils.getCurrentUserId();
                String currentUserEmail = SecurityUtils.getCurrentUserEmail();

                // 2. Tạo Command
                InviteMemberCommand command = new InviteMemberCommand(
                                workspaceId,
                                currentUserId,
                                currentUserEmail,
                                request.getEmail(),
                                request.getRole());

                // 3. Gọi Handler xử lý
                inviteMemberHandler.handle(command);

                // 4. Trả về kết quả (Data = null vì chỉ cần thông báo thành công)
                return ResponseEntity.ok(
                                ApiResponse.success(null, "Đã gửi lời mời thành công!"));
        }

        // API Validate sinh ra để trả lời các câu hỏi đó. Dựa vào kết quả trả về,
        // Frontend sẽ quyết định:

        // Nếu Email chưa tồn tại: Hiện form Đăng ký (Điền sẵn email huy@gmail.com).

        // Nếu Email đã tồn tại: Hiện form Đăng nhập. (đi chung với api dưới )
        @GetMapping("/validate")
        public ResponseEntity<ApiResponse<InvitationValidationResponse>> validateToken(
                        @RequestParam("token") String token) {

                // 1. Gọi Handler -> Nhận về Application DTO (com.project...application.dto...)
                var appResult = validateInvitationHandler.handle(token);

                // 2. Map sang Presentation DTO (com.project...presentation...dto...)
                // (Việc này tuy hơi cực nhưng giúp code cực kỳ an toàn và chuyên nghiệp)
                var response = InvitationValidationResponse.builder()
                                .isValid(appResult.isValid())
                                .email(appResult.getEmail())
                                .workspaceName(appResult.getWorkspaceName())
                                .workspaceId(appResult.getWorkspaceId())
                                .role(appResult.getRole())
                                .isUserExist(appResult.isUserExist())
                                .build();

                return ResponseEntity.ok(ApiResponse.success(response, "Token hợp lệ"));
        }

        @PostMapping("/accept")
        @Operation(summary = "Chấp nhận lời mời", description = "User đăng nhập chấp nhận tham gia workspace thông qua token")
        public ResponseEntity<ApiResponse<AcceptInvitationResponse>> acceptInvitation(
                        @RequestBody @Valid AcceptInvitationRequest request) {

                // 1. [TỰ ĐỘNG] Lấy ID và Email từ SecurityUtils
                // UUID currentUserId = SecurityUtils.getCurrentUserId();
                // String currentUserEmail = SecurityUtils.getCurrentUserEmail();

                UUID currentUserId = UUID.fromString("4eb01951-a88e-43ec-abbd-43dcb71cfbae");
                String currentUserEmail = "n22dccn035@student.ptithcm.edu.vn"; // Phải khớp với email trong token test
                                                                               // lúc nãy

                // 2. Tạo Command
                AcceptInvitationCommand command = new AcceptInvitationCommand(
                                request.getToken(),
                                currentUserId,
                                currentUserEmail);

                // 3. Gọi Handler
                // 💡 LƯU Ý: Bạn nên sửa Handler để nó return UUID workspaceId nhé
                UUID joinedWorkspaceId = acceptInvitationHandler.handle(command);

                // 4. Tạo Response DTO
                AcceptInvitationResponse responseData = AcceptInvitationResponse.builder()
                                .workspaceId(joinedWorkspaceId)
                                .message("Chào mừng! Bạn đã tham gia Workspace thành công.")
                                .build();

                return ResponseEntity.ok(
                                ApiResponse.success(responseData, "Tham gia thành công"));
        }

}
