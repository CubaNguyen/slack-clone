package com.project.workspace_service.infrastructure.persistence.mapper;

import com.project.workspace_service.domain.aggregate.channel.Channel;
import com.project.workspace_service.domain.enums.ChannelType;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.WorkspaceJpaEntity;

public class ChannelMapper {
    public static ChannelJpaEntity toJpa(Channel domain) {
        if (domain == null)
            return null;

        ChannelJpaEntity entity = new ChannelJpaEntity();
        entity.setId(domain.getId());

        // --- FIX LỖI TẠI ĐÂY ---
        // Thay vì entity.setWorkspaceId(id), ta set Object
        if (domain.getWorkspaceId() != null) {
            WorkspaceJpaEntity workspaceRef = new WorkspaceJpaEntity();
            workspaceRef.setId(domain.getWorkspaceId());
            entity.setWorkspace(workspaceRef); // Hibernate tự lấy ID để lưu vào cột workspace_id
        }

        entity.setName(domain.getName());

        // Convert Enum Type
        if (domain.getType() != null) {
            try {
                entity.setType(ChannelType.valueOf(domain.getType().name()));
            } catch (IllegalArgumentException e) {
                entity.setType(ChannelType.PUBLIC); // Default
            }
        }

        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}