package com.project.workspace_service.application.command.kickmember;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.domain.aggregate.workspace.WorkspaceMember;
import com.project.workspace_service.domain.enums.WorkspaceRole;
import com.project.workspace_service.domain.repository.ChannelMemberRepository;
import com.project.workspace_service.domain.repository.WorkspaceMemberRepository;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KickMemberHandler {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional
    public void handle(KickMemberCommand command) {

        // 1. Validation cơ bản: Không được tự kick chính mình (Dùng chức năng Leave)
        if (command.adminId().equals(command.targetUserId())) {
            throw new AppException(ErrorCode.CANNOT_KICK_SELF);
        }

        // 2. Lấy thông tin người đi kick (Admin)
        WorkspaceMember admin = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(command.workspaceId(), command.adminId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_IN_WORKSPACE));

        // CHECK QUYỀN: Phải là ADMIN hoặc OWNER mới được kick
        if (!WorkspaceRole.ADMIN.equals(admin.getRole()) && !WorkspaceRole.OWNER.equals(admin.getRole())) {
            throw new AppException(ErrorCode.NO_PERMISSION_TO_KICK);
        }

        // 3. Lấy thông tin nạn nhân (Target)
        WorkspaceMember target = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(command.workspaceId(), command.targetUserId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_IN_WORKSPACE));
        if (admin.getUserId().equals(target.getUserId())) {
            throw new AppException(ErrorCode.CANNOT_KICK_YOURSELF); // "Bạn không thể tự kick chính mình, hãy dùng chức
                                                                    // năng 'Rời Workspace'"
        }
        // CHECK BẢO VỆ: Không ai được kick OWNER
        if (WorkspaceRole.OWNER.equals(target.getRole())) {
            throw new AppException(ErrorCode.CANNOT_KICK_OWNER);
        }

        // (Tuỳ chọn) Admin có được kick Admin không?
        // Logic thông thường: Owner > Admin > Member.
        // Nếu Admin kick Admin -> Ok. Nếu muốn chặt hơn thì thêm logic check ở đây.

        // 4. ACTION 1: Soft Delete khỏi Workspace (Set left_at)
        target.leave();
        workspaceMemberRepository.save(target);

        // 5. ACTION 2: Hard Delete khỏi tất cả Channel thuộc Workspace đó
        // (Đây là hàm custom query chúng ta viết ở bước 2)
        channelMemberRepository.deleteAllByWorkspaceIdAndUserId(command.workspaceId(), command.targetUserId());
    }
}