package com.project.workspace_service.application.query.invitation.validateInvitation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.application.dto.InvitationValidationResponse;
import com.project.workspace_service.domain.aggregate.invitation.Invitation;
import com.project.workspace_service.domain.dto.InviteeCandidate;
import com.project.workspace_service.domain.enums.InvitationStatus;
import com.project.workspace_service.domain.gateway.UserGateway;
import com.project.workspace_service.domain.repository.InvitationQueryRepository;
import com.project.workspace_service.domain.repository.WorkspaceQueryRepository; // Dùng cái Query Repo ông đã có
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateInvitationHandler {

    private final InvitationQueryRepository invitationRepository;
    private final WorkspaceQueryRepository workspaceQueryRepository;
    private final UserGateway userGateway;

    @Transactional(readOnly = true) // Chỉ đọc, tối ưu hiệu năng
    public InvitationValidationResponse handle(String token) {
        // 1. Tìm trong DB xem có token này không
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVITATION_NOT_FOUND));

        // 2. Check trạng thái: Phải là PENDING mới được
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new AppException(ErrorCode.INVITATION_ALREADY_USED);
        }

        // 3. Check hạn sử dụng
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVITATION_EXPIRED);
        }

        // 4. Lấy tên Workspace để trả về cho đẹp đội hình
        String workspaceName = workspaceQueryRepository.getNameById(invitation.getWorkspaceId())
                .orElse("Unknown Workspace");

        boolean isUserExist = false;
        try {
            // Gọi qua Gateway (đã có sẵn logic Redis -> gRPC)
            InviteeCandidate candidate = userGateway.getUserByEmail(invitation.getEmail());

            // Nếu khác null tức là có user
            isUserExist = (candidate != null);

        } catch (AppException e) {
            // Nếu User Service chết (ErrorCode.USER_SERVICE_UNAVAILABLE)
            // Ông có thể chọn: Throw lỗi luôn hoặc coi như user chưa tồn tại (nhưng rủi ro)
            // Khuyên dùng: Log lại và throw lỗi để Client biết hệ thống đang bận
            log.error("CRITICAL: Cannot validate user existence via Identity Service. Error: {}", e.getMessage());
            throw e;
        }
        // 5. Trả về kết quả Xanh Chín
        return InvitationValidationResponse.builder()
                .isValid(true)
                .email(invitation.getEmail())
                .workspaceId(invitation.getWorkspaceId())
                .workspaceName(workspaceName)
                .role(invitation.getRole().name())
                .isUserExist(isUserExist)
                .build();
    }
}