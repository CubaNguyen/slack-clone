package com.project.workspace_service.presentation.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workspace_service.application.command.createchannel.CreateChannelCommand;
import com.project.workspace_service.application.command.createchannel.CreateChannelHandler;
import com.project.workspace_service.application.command.joinchannel.JoinChannelCommand;
import com.project.workspace_service.application.command.joinchannel.JoinChannelHandler;
import com.project.workspace_service.application.command.leavechannel.LeaveChannelCommand;
import com.project.workspace_service.application.command.leavechannel.LeaveChannelHandler;
import com.project.workspace_service.presentation.rest.dto.request.CreateChannelRequest;
import com.project.workspace_service.presentation.rest.dto.response.CreateChannelResponse;
import com.project.workspace_service.shared.response.ApiResponse;
import com.project.workspace_service.shared.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor // Tự tạo constructor cho final field
@Tag(name = "Channels", description = "Các API quản lý Channel")
public class ChannelController {

        private final CreateChannelHandler createChannelHandler;
        private final JoinChannelHandler joinHandler;
        private final LeaveChannelHandler leaveChannelHandler;

        @PostMapping("workspaces/{workspaceId}")
        @Operation(summary = "Tạo Channel mới", description = "Tạo channel Public hoặc Private trong workspace")
        public ResponseEntity<ApiResponse<CreateChannelResponse>> createChannel(
                        @PathVariable UUID workspaceId,
                        @RequestBody @Valid CreateChannelRequest request) {

                // 1. [TỰ ĐỘNG] Lấy User ID từ Header
                UUID currentUserId = SecurityUtils.getCurrentUserId();
                // 2. Map Request -> Command
                CreateChannelCommand command = new CreateChannelCommand(
                                workspaceId,
                                currentUserId,
                                request.getName(),
                                request.getType());

                // 3. Gọi Handler xử lý
                UUID channelId = createChannelHandler.handle(command);

                // 4. Trả về kết quả chuẩn format
                CreateChannelResponse responseData = CreateChannelResponse.builder()
                                .id(channelId)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(responseData, "Tạo channel thành công"));
        }

        @PostMapping("/workspace/{workspaceId}/{channelId}/join") // 👈 URL rõ ràng: /channels/workspace/{id}/{id}/join
        @Operation(summary = "Tham gia Channel", description = "User hiện tại tham gia vào một channel cụ thể")
        public ResponseEntity<ApiResponse<Void>> joinChannel(
                        @PathVariable UUID workspaceId,
                        @PathVariable UUID channelId) {

                // 1. [TỰ ĐỘNG] Lấy User ID từ Header/Token
                // UUID currentUserId = SecurityUtils.getCurrentUserId();
                UUID currentUserId = UUID.fromString("4eb01951-a88e-43ec-abbd-43dcb71cfbae");

                // 2. Tạo Command
                JoinChannelCommand command = new JoinChannelCommand(
                                workspaceId,
                                channelId,
                                currentUserId);

                // 3. Gọi Handler xử lý (Handler này trả về void nên không cần biến hứng)
                joinHandler.handle(command);

                // 4. Trả về kết quả thành công (Data = null)
                return ResponseEntity.ok(
                                ApiResponse.success(null, "Tham gia channel thành công"));
        }

        // Inject handler

        // ... import SecurityUtils, ApiResponse, Operation...

        @DeleteMapping("/workspace/{workspaceId}/{channelId}/leave") // 👈 URL rõ ràng
        @Operation(summary = "Rời khỏi Channel", description = "User hiện tại rời khỏi một channel cụ thể")
        public ResponseEntity<ApiResponse<Void>> leaveChannel(
                        @PathVariable UUID workspaceId,
                        @PathVariable UUID channelId) {

                // 1. [TỰ ĐỘNG] Lấy User ID từ Token
                UUID currentUserId = UUID.fromString("4eb01951-a88e-43ec-abbd-43dcb71cfbae");
                // lúc nãy
                // 2. Tạo Command (Nên dùng Command để thống nhất style với Join)
                LeaveChannelCommand command = new LeaveChannelCommand(
                                workspaceId,
                                channelId,
                                currentUserId);

                // 3. Gọi Handler xử lý
                leaveChannelHandler.handle(command);

                // 4. Trả về kết quả thành công
                return ResponseEntity.ok(
                                ApiResponse.success(null, "Đã rời kênh thành công."));
        }

}