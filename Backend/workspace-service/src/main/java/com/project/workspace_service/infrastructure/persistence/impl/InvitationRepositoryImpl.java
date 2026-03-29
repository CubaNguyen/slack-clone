package com.project.workspace_service.infrastructure.persistence.impl;

import org.springframework.stereotype.Repository;

import com.project.workspace_service.domain.aggregate.invitation.Invitation;
import com.project.workspace_service.domain.repository.InvitationRepository;
import com.project.workspace_service.infrastructure.persistence.jpa.entity.InvitationJpaEntity;
import com.project.workspace_service.infrastructure.persistence.jpa.repository.InvitationJpaRepository;
import com.project.workspace_service.infrastructure.persistence.mapper.InvitationMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InvitationRepositoryImpl implements InvitationRepository { // Implement Interface của Domain

    private final InvitationJpaRepository springDataRepo; // "Vũ khí" của Spring Data

    @Override
    public void save(Invitation invitation) {
        // BƯỚC 1: Chuyển đổi (Đây là lý do chính cần lớp Impl này)
        InvitationJpaEntity jpaEntity = InvitationMapper.toJpa(invitation);

        springDataRepo.save(jpaEntity);
    }
}