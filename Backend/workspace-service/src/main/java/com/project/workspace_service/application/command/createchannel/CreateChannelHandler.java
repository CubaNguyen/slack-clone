package com.project.workspace_service.application.command.createchannel;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.workspace_service.domain.aggregate.channel.Channel;
import com.project.workspace_service.domain.aggregate.workspace.Workspace;
import com.project.workspace_service.domain.event.ChannelCreatedEvent;
import com.project.workspace_service.domain.repository.ChannelRepository;
import com.project.workspace_service.domain.repository.WorkspaceRepository;
import com.project.workspace_service.domain.service.ChannelDomainService;
import com.project.workspace_service.shared.DomainEventPublisher;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateChannelHandler {

    private final WorkspaceRepository workspaceRepository;
    private final ChannelRepository channelRepository;
    private final ChannelDomainService channelDomainService;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public UUID handle(CreateChannelCommand command) {
        // 1. Kiểm tra Workspace tồn tại
        Workspace workspace = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND)); // Dùng ErrorCode

        // 2. [QUAN TRỌNG] Kiểm tra trùng tên Channel trong Workspace
        // Phải check ở đây để tránh lỗi Unique Constraint 500 từ DB
        if (channelRepository.existsByWorkspaceIdAndName(command.workspaceId(), command.name())) {
            throw new AppException(ErrorCode.CHANNEL_ALREADY_EXISTS); // Ném lỗi CH_001
        }

        // 3. Lấy role của User
        String userRole = workspaceRepository.getUserRole(command.workspaceId(), command.userId());
        if (userRole == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 4. Gọi Domain Service
        Channel channel = channelDomainService.createChannel(
                workspace,
                command.name(),
                command.type(),
                command.userId(),
                userRole);

        // 5. Lưu và Phát tán event
        channelRepository.save(channel);

        eventPublisher.publish(new ChannelCreatedEvent(
                channel.getId(),
                channel.getWorkspaceId(),
                command.userId(),
                channel.getName(), // <--- Đã có tên
                channel.getType() // <--- Đã có Type (Enum tự convert sang String trong Event)
        ));

        // (Optional) Xóa event cũ trong entity để tránh bắn 2 lần (nếu entity có tự
        // tạo)
        channel.clearDomainEvents();

        return channel.getId();
    }
}