package com.project.workspace_service.infrastructure.persistence.impl;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.project.workspace_service.domain.aggregate.channel.Channel;
import com.project.workspace_service.domain.repository.ChannelRepository;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.repository.ChannelJpaRepository;
import com.project.workspace_service.infrastructure.persistence.mapper.ChannelMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChannelRepositoryImpl implements ChannelRepository {

    private final ChannelJpaRepository springDataRepo;

    @Override
    public void save(Channel channel) {
        // Chuyển đổi từ Domain sang JPA Entity trước khi save
        ChannelJpaEntity jpaEntity = ChannelMapper.toJpa(channel);
        springDataRepo.save(jpaEntity);
    }

    @Override
    public boolean existsByWorkspaceIdAndName(UUID workspaceId, String name) {
        // Gọi xuống Spring Data JPA để kiểm tra
        return springDataRepo.existsByWorkspaceIdAndName(workspaceId, name);
    }
}