package com.project.workspace_service.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.workspace_service.domain.enums.InvitationRole;
import com.project.workspace_service.domain.enums.InvitationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@Builder
@AllArgsConstructor // Thường đi kèm với Builder
@NoArgsConstructor
public class InvitationJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceJpaEntity workspace;

    @Column(name = "inviter_id", nullable = false)
    private UUID inviterId;
    @Column(name = "inviter_email") // Lưu luôn email người mời
    private String inviterEmail;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationRole role;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING; // Thêm trường này vào

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}