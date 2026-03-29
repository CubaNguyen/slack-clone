package com.project.workspace_service.domain.aggregate.invitation;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.domain.enums.InvitationRole;
import com.project.workspace_service.domain.enums.InvitationStatus;
import com.project.workspace_service.shared.AggregateRoot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // Lombok cần AllArgsConstructor để hoạt động
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Invitation extends AggregateRoot {
    private final UUID id;
    private final UUID workspaceId;
    private final UUID inviterId;
    // Lưu email người mời để tiện cho việc gửi email
    private final String inviterEmail;
    private final String email;
    private final InvitationRole role;
    private final String token;
    private InvitationStatus status;
    private LocalDateTime acceptedAt;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    // Factory Method cho Business Logic
    public static Invitation create(UUID workspaceId, UUID inviterId, String inviterEmail, String email,
            String roleStr) {
        InvitationRole role = InvitationRole.valueOf(roleStr.toUpperCase());
        return Invitation.builder()
                .id(UUID.randomUUID())
                .workspaceId(workspaceId)
                .inviterId(inviterId)
                .inviterEmail(inviterEmail)
                .email(email)
                .role(role)
                .token(UUID.randomUUID().toString())
                .status(InvitationStatus.PENDING)
                .acceptedAt(null) // Mặc định khi tạo mới là null
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();
    }
}