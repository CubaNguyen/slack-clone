package com.project.workspace_service.presentation.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workspace_service.application.command.createworkspace.CreateWorkspaceCommand;
import com.project.workspace_service.application.command.createworkspace.CreateWorkspaceHandler;
import com.project.workspace_service.application.dto.BrowseChannelDto;
import com.project.workspace_service.application.dto.SidebarChannelDto;
import com.project.workspace_service.application.dto.WorkspaceDto;
import com.project.workspace_service.application.query.channel.ListChannelsQuery;
import com.project.workspace_service.application.query.channel.getSidebar.GetMySidebarChannelsHandler;
import com.project.workspace_service.application.query.channel.listPublic.ListPublicChannelsHandler;
import com.project.workspace_service.application.query.workspace.list.ListWorkspacesHandler;
import com.project.workspace_service.application.query.workspace.list.ListWorkspacesQuery;
import com.project.workspace_service.presentation.rest.dto.request.CreateWorkspaceRequest;
import com.project.workspace_service.presentation.rest.dto.response.WorkspaceResponse;
import com.project.workspace_service.shared.response.ApiResponse; // Import cái class chuẩn bạn đã tạo
import com.project.workspace_service.shared.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor // Tự tạo constructor cho final field
@Tag(name = "Workspace", description = "Các API quản lý Workspace")
public class WorkspaceController {

        private final CreateWorkspaceHandler createWorkspaceHandler;
        private final ListWorkspacesHandler listHandler;
        private final GetMySidebarChannelsHandler sidebarHandler;
        private final ListPublicChannelsHandler listPublicChannelsHandler;

        @PostMapping
        @Operation(summary = "Tạo mới Workspace", description = "API tạo workspace mới và set user hiện tại làm Owner")
        public ResponseEntity<ApiResponse<WorkspaceResponse>> createWorkspace(
                        @RequestBody @Valid CreateWorkspaceRequest request) {

                // 1. Giả lập User ID (Sau này lấy từ SecurityContextHolder)
                UUID currentUserId = SecurityUtils.getCurrentUserId();
                // 2. Mapping Request -> Command
                CreateWorkspaceCommand command = new CreateWorkspaceCommand(
                                request.getName(),
                                request.getSlug(),
                                currentUserId);

                // 3. Gọi Handler xử lý (Handler trả về UUID)
                UUID workspaceId = createWorkspaceHandler.handle(command);

                // 4. Tạo Response DTO
                WorkspaceResponse responseData = WorkspaceResponse.builder()
                                .id(workspaceId)
                                .name(request.getName())
                                .slug(request.getSlug())
                                .build();

                // 5. Trả về JSON chuẩn format
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(responseData, "Tạo workspace thành công!"));
        }

        @GetMapping("/mine")
        @Operation(summary = "Lấy danh sách Workspace của tôi", description = "Trả về danh sách toàn bộ workspace mà user hiện tại đang tham gia hoặc sở hữu.")
        public ResponseEntity<ApiResponse<List<WorkspaceDto>>> getMyWorkspaces() {

                UUID currentUserId = SecurityUtils.getCurrentUserId();

                List<WorkspaceDto> result = listHandler.handle(new ListWorkspacesQuery(currentUserId));
                return ResponseEntity.ok(ApiResponse.success(result, "Lấy danh sách workspace thành công!"));
        }

        @GetMapping("/{workspaceId}/sidebar/channels")
        @Operation(summary = "Lấy danh sách Channel ở side bar", description = "Lấy toàn bộ channel (Public) và các channel Private mà user này tham gia trong Workspace.")
        public ResponseEntity<ApiResponse<List<SidebarChannelDto>>> getSidebarChannels(
                        @PathVariable UUID workspaceId) {

                // 1. [TỰ ĐỘNG] Lấy User ID từ Header (x-user-id)
                // Không còn fix cứng "mockUserId" nữa
                UUID currentUserId = SecurityUtils.getCurrentUserId();

                // 2. Gọi Query Handler
                List<SidebarChannelDto> channels = sidebarHandler.handle(
                                new ListChannelsQuery(workspaceId, currentUserId));

                // 3. Trả về JSON chuẩn format (bọc trong ApiResponse)
                return ResponseEntity.ok(
                                ApiResponse.success(channels, "Lấy danh sách channel thành công"));
        }

        @GetMapping("/{workspaceId}/browse/channels")
        @Operation(summary = "Lấy danh sách Channel ở browse", description = "Lấy toàn bộ channel (Public) trong Workspace.")
        public ResponseEntity<ApiResponse<List<BrowseChannelDto>>> browseChannels(
                        @PathVariable UUID workspaceId) {

                // 1. [TỰ ĐỘNG] Lấy User ID từ Header (x-user-id)
                // Không còn fix cứng "mockUserId" nữa
                UUID currentUserId = SecurityUtils.getCurrentUserId();

                // 2. Gọi Query Handler
                List<BrowseChannelDto> channels = listPublicChannelsHandler.handle(
                                new ListChannelsQuery(workspaceId, currentUserId));

                // 3. Trả về JSON chuẩn format (bọc trong ApiResponse)
                return ResponseEntity.ok(
                                ApiResponse.success(channels, "Lấy danh sách channel thành công"));
        }

}