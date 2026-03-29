package com.project.workspace_service.application.query.channel.listPublic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.workspace_service.application.dto.BrowseChannelDto;
import com.project.workspace_service.application.query.channel.ListChannelsQuery;
import com.project.workspace_service.domain.repository.ChannelQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListPublicChannelsHandler {

    private final ChannelQueryRepository channelQueryRepository;

    public List<BrowseChannelDto> handle(ListChannelsQuery query) {
        // Logic Sidebar: Chỉ lấy những gì tôi đã tham gia [Suy luận]
        return channelQueryRepository.findAllPublicChannels(query.workspaceId(), query.userId());
    }
}