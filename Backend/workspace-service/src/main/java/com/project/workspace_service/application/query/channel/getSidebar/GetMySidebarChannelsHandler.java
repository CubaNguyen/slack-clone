package com.project.workspace_service.application.query.channel.getSidebar;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.workspace_service.application.dto.SidebarChannelDto;
import com.project.workspace_service.application.query.channel.ListChannelsQuery;
import com.project.workspace_service.domain.repository.ChannelQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMySidebarChannelsHandler {

    private final ChannelQueryRepository channelQueryRepository;

    public List<SidebarChannelDto> handle(ListChannelsQuery query) {
        // Logic Sidebar: Chỉ lấy những gì tôi đã tham gia [Suy luận]
        return channelQueryRepository.findMySidebarChannels(query.workspaceId(), query.userId());
    }
}