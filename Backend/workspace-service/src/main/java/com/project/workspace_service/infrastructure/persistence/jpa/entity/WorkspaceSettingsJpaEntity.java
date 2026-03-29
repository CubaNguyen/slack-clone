package com.project.workspace_service.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspace_settings")
@Getter
@Setter
public class WorkspaceSettingsJpaEntity {

    @Id
    @Column(name = "workspace_id")
    private UUID id;

    // Chia sẻ ID với Workspace (PK cũng là FK)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "workspace_id")
    private WorkspaceJpaEntity workspace;

    @Column(name = "allow_member_create_channel")
    private Boolean allowMemberCreateChannel = true; // Default SQL

    @Column(name = "allow_member_archive_channel")
    private Boolean allowMemberArchiveChannel = false;

    @Column(name = "allow_member_invite_guest")
    private Boolean allowMemberInviteGuest = false;

    @Column(name = "message_retention_days")
    private Integer messageRetentionDays = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}