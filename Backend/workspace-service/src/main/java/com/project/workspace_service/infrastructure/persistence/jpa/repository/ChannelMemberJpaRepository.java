package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.infrastructure.persistence.jpa.entity.ChannelMemberJpaEntity;

import jakarta.transaction.Transactional;

@Repository
public interface ChannelMemberJpaRepository extends JpaRepository<ChannelMemberJpaEntity, UUID> {
    @Modifying // Bắt buộc cho các thao tác DELETE/UPDATE
    @Transactional // Cần thiết để thực thi lệnh xóa
    @Query("DELETE FROM ChannelMemberJpaEntity c WHERE c.channel.id = :channelId AND c.userId = :userId")
    void deleteByChannelIdAndUserId(UUID channelId, UUID userId);

    Optional<ChannelMemberJpaEntity> findByChannelIdAndUserId(UUID channelId, UUID userId);

    @Modifying
    @Transactional
    @Query("""
                DELETE FROM ChannelMemberJpaEntity cm
                WHERE cm.userId = :userId
                AND cm.channel.id IN (
                    SELECT c.id FROM ChannelJpaEntity c WHERE c.workspace.id = :workspaceId
                )
            """)
    void deleteAllByWorkspaceIdAndUserId(
            @Param("workspaceId") UUID workspaceId,
            @Param("userId") UUID userId);
}