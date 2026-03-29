package com.project.workspace_service.application.command.joinchannel;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.application.dto.ChannelDto;
import com.project.workspace_service.domain.aggregate.channel.ChannelMember;
import com.project.workspace_service.domain.enums.ChannelRole;
import com.project.workspace_service.domain.repository.ChannelMemberQueryRepository;
import com.project.workspace_service.domain.repository.ChannelMemberRepository;
import com.project.workspace_service.domain.repository.ChannelQueryRepository;
import com.project.workspace_service.domain.repository.WorkspaceQueryRepository;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JoinChannelHandler {

    private final ChannelQueryRepository channelQueryRepository;
    private final ChannelMemberRepository channelMemberRepository;

    private final ChannelMemberQueryRepository channelMemberQueryRepository;

    private final WorkspaceQueryRepository workspaceQueryRepository;

    @Transactional
    public void handle(JoinChannelCommand command) {
        // 1. CHECK: User có thuộc Workspace không?
        boolean inWorkspace = workspaceQueryRepository.isUserInWorkspace(command.workspaceId(), command.userId());
        if (!inWorkspace) {
            throw new AppException(ErrorCode.NOT_IN_WORKSPACE); // Trả về lỗi 403 sạch sẽ
        }

        // 2. CHECK: Channel có tồn tại không?
        ChannelDto channel = channelQueryRepository.findById(command.channelId())
                .orElseThrow(() -> new AppException(ErrorCode.CHANNEL_NOT_FOUND));
        // 3. CHECK: User đã là thành viên của kênh chưa?
        boolean alreadyJoined = channelMemberQueryRepository.existsByChannelIdAndUserId(
                command.channelId(), command.userId());

        System.err.println("---------" + command.userId());
        if (alreadyJoined) {
            throw new AppException(ErrorCode.ALREADY_MEMBER); // Thông báo rõ ràng thay vì trả về 500
        }
        // 4. CHECK: Kênh có phải là PUBLIC không?
        if (!"PUBLIC".equalsIgnoreCase(channel.type())) {
            throw new AppException(ErrorCode.PRIVATE_CHANNEL_ACCESS_DENIED);
        }

        // 5. ACTION: Tạo thành viên mới
        ChannelMember newMember = ChannelMember.builder()
                .id(UUID.randomUUID()) // Luôn nhớ gán ID
                .channelId(channel.id())
                .userId(command.userId())
                .role(ChannelRole.MEMBER) // Mặc định join tự do là MEMBER
                .joinedAt(LocalDateTime.now())
                .build();

        channelMemberRepository.save(newMember);

        // TODO: Có thể bắn event UserJoinedChannelEvent để update UI real-time
    }
}