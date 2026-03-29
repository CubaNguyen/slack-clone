package com.project.workspace_service.application.command.acceptinvitation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.application.dto.DefaultChannelDto;
import com.project.workspace_service.domain.aggregate.channel.ChannelMember;
import com.project.workspace_service.domain.aggregate.invitation.Invitation;
import com.project.workspace_service.domain.aggregate.workspace.WorkspaceMember;
import com.project.workspace_service.domain.enums.ChannelRole;
import com.project.workspace_service.domain.enums.InvitationStatus;
import com.project.workspace_service.domain.enums.WorkspaceRole;
import com.project.workspace_service.domain.repository.ChannelMemberRepository;
import com.project.workspace_service.domain.repository.ChannelQueryRepository;
import com.project.workspace_service.domain.repository.InvitationQueryRepository;
import com.project.workspace_service.domain.repository.InvitationRepository;
import com.project.workspace_service.domain.repository.WorkspaceMemberRepository;
import com.project.workspace_service.domain.repository.WorkspaceQueryRepository;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcceptInvitationHandler {

    private final InvitationRepository invitationRepository;
    private final InvitationQueryRepository invitationQueryRepository;

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceQueryRepository workspaceRepository; // Để check exist member
    private final ChannelQueryRepository channelQueryRepository;
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional(rollbackFor = Exception.class)
    public UUID handle(AcceptInvitationCommand command) {
        // 1. Tìm Lời mời
        Invitation invitation = invitationQueryRepository.findByToken(command.token())
                .orElseThrow(() -> new AppException(ErrorCode.INVITATION_NOT_FOUND));

        // 2. Validate: Hết hạn chưa?
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new AppException(ErrorCode.INVITATION_EXPIRED);
        }

        // 3. Validate: Trạng thái phải là PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new AppException(ErrorCode.INVITATION_ALREADY_USED);
        }

        // 4. [SECURITY CHECK] Email đăng nhập có khớp với Email được mời không?
        // Cái này cực quan trọng để chống hack/chiếm quyền
        if (!invitation.getEmail().equalsIgnoreCase(command.currentUserEmail())) {
            throw new AppException(ErrorCode.INVITATION_EMAIL_MISMATCH);
        }

        // 5. Validate: Đã là thành viên từ trước chưa?
        if (workspaceRepository.existsByWorkspaceIdAndUserId(invitation.getWorkspaceId(), command.currentUserId())) {
            // Nếu đã vào rồi thì thôi, coi như thành công luôn để tránh lỗi
            // Hoặc throw Exception tùy nghiệp vụ của ông
            return invitation.getWorkspaceId();
        }

        // 6. ACTION: Thêm vào Workspace Member
        WorkspaceMember newMember = WorkspaceMember.builder()
                .id(UUID.randomUUID())
                .workspaceId(invitation.getWorkspaceId())
                .userId(command.currentUserId())
                .role(WorkspaceRole.valueOf(invitation.getRole().name())) // Lấy Role từ lúc mời (Admin/Member)
                .joinedAt(LocalDateTime.now())
                .build();

        workspaceMemberRepository.save(newMember);

        // 1. Lấy danh sách channel mặc định
        List<DefaultChannelDto> defaultChannels = channelQueryRepository
                .findDefaultChannelsByWorkspaceId(invitation.getWorkspaceId());

        // 2. [QUAN TRỌNG] Phải có vòng lặp này mới có biến 'channelDto' để sài
        for (DefaultChannelDto channelDto : defaultChannels) {

            ChannelMember channelMember = ChannelMember.builder()
                    .id(UUID.randomUUID())
                    .channelId(channelDto.id()) // <-- Giờ thì biến channelDto đã hợp lệ
                    .userId(command.currentUserId())
                    .role(ChannelRole.MEMBER)
                    .joinedAt(LocalDateTime.now())
                    .build();

            // Lưu từng thành viên
            channelMemberRepository.save(channelMember);
        }

        // 7. ACTION: Update trạng thái lời mời
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        return invitation.getWorkspaceId();
    }
}