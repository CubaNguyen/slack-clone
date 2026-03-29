package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.infrastructure.persistence.jpa.entity.InvitationJpaEntity;

@Repository
public interface InvitationJpaRepository extends JpaRepository<InvitationJpaEntity, UUID> {

}