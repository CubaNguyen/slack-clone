package com.project.workspace_service.infrastructure.persistence.mapper;

import com.project.workspace_service.domain.aggregate.channel.ChannelMember;
import com.project.workspace_service.domain.enums.ChannelRole;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelMemberJpaEntity;

public class ChannelMemberMapper {
    public static ChannelMemberJpaEntity toJpa(ChannelMember domain) {
        if (domain == null)
            return null;
        ChannelMemberJpaEntity jpa = new ChannelMemberJpaEntity();

        jpa.setId(domain.getId());

        // --- KHẮC PHỤC LỖI TẠI ĐÂY ---
        // Vì Entity yêu cầu Object Channel, ta tạo một object "rỗng" chỉ chứa ID
        // Hibernate sẽ tự hiểu đây là Reference Key (FK) mà không cần query DB
        if (domain.getChannelId() != null) {
            ChannelJpaEntity channelRef = new ChannelJpaEntity();
            channelRef.setId(domain.getChannelId());
            jpa.setChannel(channelRef); // Gán Object thay vì gán ID
        }

        jpa.setUserId(domain.getUserId());

        // Convert String (Domain) -> Enum (JPA)
        if (domain.getRole() != null) {
            try {
                jpa.setRole(domain.getRole());
            } catch (IllegalArgumentException e) {
                // Default nếu string sai
                jpa.setRole(ChannelRole.MEMBER);
            }
        }

        jpa.setJoinedAt(domain.getJoinedAt());
        jpa.setLeftAt(domain.getLeftAt());

        return jpa;
    }

    public static ChannelMember toDomain(ChannelMemberJpaEntity entity) {
        if (entity == null)
            return null;

        return ChannelMember.builder()
                .id(entity.getId())
                // Lấy ID từ Object quan hệ ManyToOne
                .channelId(entity.getChannel() != null ? entity.getChannel().getId() : null)
                .userId(entity.getUserId())
                .role(entity.getRole())
                .joinedAt(entity.getJoinedAt())
                .leftAt(entity.getLeftAt())
                .build();
    }
}