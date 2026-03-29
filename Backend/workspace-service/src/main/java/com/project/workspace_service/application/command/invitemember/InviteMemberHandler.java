package com.project.workspace_service.application.command.invitemember;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.domain.aggregate.invitation.Invitation;
import com.project.workspace_service.domain.dto.InviteeCandidate;
import com.project.workspace_service.domain.enums.InvitationStatus;
import com.project.workspace_service.domain.gateway.UserGateway;
import com.project.workspace_service.domain.repository.InvitationQueryRepository;
import com.project.workspace_service.domain.repository.InvitationRepository;
import com.project.workspace_service.domain.repository.WorkspaceQueryRepository;
import com.project.workspace_service.domain.service.EmailService;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;
import com.project.workspace_service.shared.utils.SecurityUtils;

// Import EmailService (Giả lập)
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InviteMemberHandler {

    private final InvitationQueryRepository invitationQueryRepository;
    private final WorkspaceQueryRepository workspaceRepository;
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final UserGateway userGateway;

    @Transactional(rollbackFor = Exception.class) // Tự động rollback nếu có lỗi
    public void handle(InviteMemberCommand command) {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        if (currentUserEmail.equalsIgnoreCase(command.email())) {
            throw new AppException(ErrorCode.CANNOT_INVITE_SELF);
        }
        // 0. [Bổ sung] Lấy thông tin Workspace trước để check tồn tại và lấy Tên
        var workspaceName = workspaceRepository.getNameById(command.workspaceId())
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));
        // 1. Lấy thông tin User (Tự động check Redis -> gRPC bên dưới)
        InviteeCandidate candidate = userGateway.getUserByEmail(command.email());
        UUID inviteeUserId = null; // Mặc định là null nếu user chưa đăng ký
        if (candidate != null) {
            // CASE A: User ĐÃ tồn tại trong hệ thống
            inviteeUserId = candidate.id();

            // Check xem đã là thành viên chưa bằng ID (Cực nhanh & Chính xác)
            // LƯU Ý: Dùng hàm existsByWorkspaceIdAndUserId thay vì Email
            boolean isMember = workspaceRepository.existsByWorkspaceIdAndUserId(command.workspaceId(), inviteeUserId);

            if (isMember) {
                throw new AppException(ErrorCode.USER_ALREADY_IN_WORKSPACE);
            }
        }
        // 2. Validate: Đã mời chưa?
        if (invitationQueryRepository.existsByEmailAndWorkspaceIdAndStatus(
                command.email(), command.workspaceId(), InvitationStatus.PENDING.name())) {
            throw new AppException(ErrorCode.INVITATION_ALREADY_SENT);
        }

        // 3. Tạo Invitation
        Invitation invitation = Invitation.create(
                command.workspaceId(),
                command.inviterId(),
                command.inviterEmail(),
                command.email(),
                command.role());

        // 4. Lưu DB (Lúc này chưa commit, vẫn nằm trong transaction)
        invitationRepository.save(invitation);

        // 5. Gửi Email (Giả lập)
        sendEmail(invitation.getEmail(), invitation.getToken(), workspaceName);
    }

    private void sendEmail(String toEmail, String token, String workspaceName) {
        try {
            // Gọi sang service gửi mail
            emailService.sendInvitationEmail(toEmail, token, workspaceName);
        } catch (Exception e) {
            // QUAN TRỌNG: Phải ném lỗi ra để kích hoạt Rollback
            // Nếu ông try-catch mà "nuốt" lỗi ở đây thì Transaction tưởng thành công -> Nó
            // vẫn lưu DB đấy
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}