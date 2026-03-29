package com.project.workspace_service.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.project.workspace_service.domain.aggregate.workspace.Workspace;
import com.project.workspace_service.domain.aggregate.workspace.WorkspaceSettings;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceSettingsJpaEntity;

@Component
public class WorkspaceMapper {

    public WorkspaceJpaEntity toEntity(Workspace domain) {
        if (domain == null)
            return null;

        WorkspaceJpaEntity entity = new WorkspaceJpaEntity();

        // 1. Map thông tin Workspace cơ bản
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSlug(domain.getSlug());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());

        // 2. MAP SETTINGS (QUAN TRỌNG NHẤT)
        if (domain.getSettings() != null) {
            WorkspaceSettingsJpaEntity settingsEntity = toSettingsEntity(domain.getSettings());

            // *** CỰC KỲ QUAN TRỌNG: THIẾT LẬP QUAN HỆ 2 CHIỀU ***
            // Nếu thiếu dòng này, @MapsId sẽ không biết lấy ID từ đâu
            settingsEntity.setWorkspace(entity);

            // Gán vào cha để Cascade hoạt động
            entity.setSettings(settingsEntity);
        }

        return entity;
    }

    private WorkspaceSettingsJpaEntity toSettingsEntity(WorkspaceSettings domainSettings) {
        WorkspaceSettingsJpaEntity entity = new WorkspaceSettingsJpaEntity();

        // Không cần set ID vì @MapsId sẽ tự lấy từ Workspace
        entity.setAllowMemberCreateChannel(domainSettings.isAllowMemberCreateChannel());
        entity.setAllowMemberArchiveChannel(domainSettings.isAllowMemberArchiveChannel());
        entity.setAllowMemberInviteGuest(domainSettings.isAllowMemberInviteGuest());
        entity.setMessageRetentionDays(domainSettings.getMessageRetentionDays());
        entity.setCreatedAt(domainSettings.getCreatedAt());
        entity.setUpdatedAt(domainSettings.getUpdatedAt());

        return entity;
    }

    // Thêm vào trong class WorkspaceMapper
    public static Workspace toDomain(WorkspaceJpaEntity entity) {
        if (entity == null)
            return null;

        // 1. Tạo Workspace từ Entity (Giả sử ông có hàm restore hoặc constructor)
        Workspace workspace = Workspace.restore(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getCreatedBy());

        // 2. Map Settings
        if (entity.getSettings() != null) {
            var s = entity.getSettings();

            // Gọi hàm restore vừa tạo ở bước 1
            var settingsDomain = WorkspaceSettings.restore(
                    s.getId(), // <--- FIX LỖI: Gọi .getId() thay vì .getWorkspaceId()
                    s.getAllowMemberCreateChannel(),
                    s.getAllowMemberArchiveChannel(),
                    s.getAllowMemberInviteGuest(),
                    s.getMessageRetentionDays(),
                    s.getCreatedAt(),
                    s.getUpdatedAt());

            // Gọi hàm setter vừa tạo ở bước 2
            workspace.setSettings(settingsDomain);
        }

        return workspace;
    }
}