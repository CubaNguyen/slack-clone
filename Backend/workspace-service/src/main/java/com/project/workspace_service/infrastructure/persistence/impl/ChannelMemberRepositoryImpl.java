package com.project.workspace_service.infrastructure.persistence.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

// Import đầy đủ
import com.project.workspace_service.domain.aggregate.channel.ChannelMember;
import com.project.workspace_service.domain.repository.ChannelMemberRepository;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelMemberJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.repository.ChannelMemberJpaRepository;
import com.project.workspace_service.infrastructure.persistence.mapper.ChannelMemberMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
// 1. SỬA TẠI ĐÂY: implements cái Domain Repository (không có chữ Jpa)
public class ChannelMemberRepositoryImpl implements ChannelMemberRepository {

    // 2. SỬA TẠI ĐÂY: Inject cái JPA Repository (có chữ Jpa)
    private final ChannelMemberJpaRepository springDataRepo;

    @Override
    public void save(ChannelMember member) {
        // Convert
        ChannelMemberJpaEntity jpaEntity = ChannelMemberMapper.toJpa(member);

        // Lưu xuống DB (Lúc này springDataRepo nhận vào Entity nên sẽ hết lỗi)
        springDataRepo.save(jpaEntity);
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        springDataRepo.deleteByChannelIdAndUserId(channelId, userId);
    }

    @Override
    public Optional<ChannelMember> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        // Gọi Spring Data JPA -> Map kết quả qua Mapper
        return springDataRepo.findByChannelIdAndUserId(channelId, userId)
                .map(ChannelMemberMapper::toDomain);
    }

    @Override
    public void deleteAllByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        springDataRepo.deleteAllByWorkspaceIdAndUserId(workspaceId, userId);
    }
}