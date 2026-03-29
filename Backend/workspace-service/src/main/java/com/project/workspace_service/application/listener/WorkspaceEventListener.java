package com.project.workspace_service.application.listener;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.domain.enums.ChannelRole;
import com.project.workspace_service.domain.event.WorkspaceCreatedEvent;
// 1. IMPORT ENTITIES
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelMemberJpaEntity; // <-- Bạn cần class này
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceMemberJpaEntity;
// 2. IMPORT REPOSITORIES
import com.project.workspace_service.infrastructure.persistence.jpa.repository.ChannelJpaRepository;
import com.project.workspace_service.infrastructure.persistence.jpa.repository.ChannelMemberJpaRepository; // <-- Bạn cần repo này
import com.project.workspace_service.infrastructure.persistence.jpa.repository.WorkspaceMemberJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor // Dùng cái này cho gọn constructor
public class WorkspaceEventListener {

    private final WorkspaceMemberJpaRepository workspaceMemberRepo;
    private final ChannelJpaRepository channelRepo;
    private final ChannelMemberJpaRepository channelMemberRepo; // <-- Inject thêm cái này

    @EventListener
    @Transactional
    public void onWorkspaceCreated(WorkspaceCreatedEvent event) {
        System.out.println("🚀 Bắt đầu Saga: WorkspaceCreatedEvent cho ID: " + event.getWorkspaceId());

        // BƯỚC 1: Gán user tạo làm OWNER của Workspace (Bảng workspace_members)
        WorkspaceMemberJpaEntity owner = WorkspaceMemberJpaEntity.createOwner(
                event.getWorkspaceId(),
                event.getOwnerId());
        workspaceMemberRepo.save(owner);

        // BƯỚC 2: Tạo 2 channel mặc định (#general, #random)
        ChannelJpaEntity general = createAndSaveChannel(event.getWorkspaceId(), "general", event.getOwnerId());
        ChannelJpaEntity random = createAndSaveChannel(event.getWorkspaceId(), "random", event.getOwnerId());

        // BƯỚC 3: Add Owner vào 2 channel vừa tạo (Bảng channel_members)

        joinChannel(general, event.getOwnerId());
        joinChannel(random, event.getOwnerId());
        System.out.println("✅ Saga hoàn tất: Owner đã được set và join vào channel #general, #random");
    }

    // --- CÁC HÀM PHỤ TRỢ (HELPER METHODS) ---

    // Hàm tạo và lưu Channel
    private ChannelJpaEntity createAndSaveChannel(UUID workspaceId, String name, UUID creatorId) {
        ChannelJpaEntity channel = ChannelJpaEntity.createDefault(workspaceId, name, creatorId);
        return channelRepo.save(channel);
    }

    // Helper Join Channel (Đã sửa theo Entity mới của bạn)
    private void joinChannel(ChannelJpaEntity channelEntity, UUID userId) {
        ChannelMemberJpaEntity member = new ChannelMemberJpaEntity();

        member.setId(UUID.randomUUID());
        member.setUserId(userId);

        // QUAN TRỌNG: Map quan hệ @ManyToOne
        member.setChannel(channelEntity);

        // QUAN TRỌNG: Dùng Enum
        member.setRole(ChannelRole.ADMIN);

        member.setJoinedAt(LocalDateTime.now());

        channelMemberRepo.save(member);
    }
}