package com.project.workspace_service.infrastructure.persistence.jpa.entity;

import com.project.workspace_service.domain.enums.ChannelType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "channels", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "workspace_id", "name" })
})
@Getter
@Setter
public class ChannelJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceJpaEntity workspace;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public static ChannelJpaEntity createDefault(UUID workspaceId, String name, UUID ownerId) {
        ChannelJpaEntity entity = new ChannelJpaEntity();
        entity.setId(UUID.randomUUID());

        WorkspaceJpaEntity ws = new WorkspaceJpaEntity();
        ws.setId(workspaceId);
        entity.setWorkspace(ws);

        entity.setName(name);
        entity.setType(ChannelType.PUBLIC); // Mặc định là Public
        entity.setCreatedBy(ownerId);
        entity.setDefault(true); // Đánh dấu là channel mặc định
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }
}