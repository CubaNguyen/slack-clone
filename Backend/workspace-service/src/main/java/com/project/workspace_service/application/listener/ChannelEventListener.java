package com.project.workspace_service.application.listener;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.project.workspace_service.domain.aggregate.channel.ChannelMember;
import com.project.workspace_service.domain.enums.ChannelRole;
import com.project.workspace_service.domain.event.ChannelCreatedEvent;
import com.project.workspace_service.domain.repository.ChannelMemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelEventListener {

    private final ChannelMemberRepository channelMemberRepo; // Repository cho bảng channel_members

    @EventListener
    @Transactional
    public void onChannelCreated(ChannelCreatedEvent event) {
        // [Suy luận]: Khi kênh vừa tạo, người tạo mặc định là Admin của kênh đó
        ChannelMember creator = ChannelMember.builder()
                .id(UUID.randomUUID())
                .channelId(event.getChannelId())
                .userId(event.getCreatorId())
                .role(ChannelRole.ADMIN) // Role trong channel
                .joinedAt(LocalDateTime.now())
                .build();

        channelMemberRepo.save(creator);
        System.out.println("✅ Saga: Đã tự động thêm người tạo vào Channel " + event.getChannelId());
    }
}