
package com.project.workspace_service.infrastructure.persistence.jpa.repository;

import com.project.workspace_service.infrastructure.outbox.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    // Tìm các event chưa xử lý (processedAt = null) để job quét
    List<OutboxEventJpaEntity> findByProcessedAtIsNullOrderByCreatedAtAsc();
}