package com.project.workspace_service.application.command.leavechannel;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.application.dto.ChannelDto;
import com.project.workspace_service.domain.aggregate.channel.ChannelMember;
import com.project.workspace_service.domain.enums.ChannelRole;
import com.project.workspace_service.domain.repository.ChannelMemberQueryRepository;
import com.project.workspace_service.domain.repository.ChannelMemberRepository;
import com.project.workspace_service.domain.repository.ChannelQueryRepository;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveChannelHandler {

    private final ChannelQueryRepository channelQueryRepository;
    private final ChannelMemberQueryRepository channelMemberQueryRepository;
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional
    public void handle(LeaveChannelCommand command) {
        // 1. Lấy thông tin Channel để check IsDefault
        ChannelDto channel = channelQueryRepository.findById(command.channelId())
                .orElseThrow(() -> new AppException(ErrorCode.CHANNEL_NOT_FOUND));

        // [RULE 1] Không được rời kênh mặc định (Ví dụ #general)
        if (channel.isDefault()) {
            throw new AppException(ErrorCode.CANNOT_LEAVE_DEFAULT_CHANNEL);
        }

        // 2. Lấy thông tin thành viên hiện tại
        ChannelMember member = channelMemberRepository
                .findByChannelIdAndUserId(command.channelId(), command.userId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_CHANNEL));

        // [RULE 2 - STRICT MODE] Check Last Admin
        if (ChannelRole.ADMIN.equals(member.getRole())) {
            long adminCount = channelMemberQueryRepository.countAdminsByChannelId(command.channelId());
            // Nếu chỉ còn 1 admin (chính là mình) -> CHẶN
            if (adminCount <= 1) {
                throw new AppException(ErrorCode.CANNOT_LEAVE_LAST_ADMIN);
            }
        }

        // 3. Thực hiện xóa (Rời kênh)
        channelMemberRepository.deleteByChannelIdAndUserId(command.channelId(), command.userId());
    }
}