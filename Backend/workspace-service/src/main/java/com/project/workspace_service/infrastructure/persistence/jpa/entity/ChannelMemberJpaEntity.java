package com.project.workspace_service.infrastructure.persistence.jpa.entity;

import com.project.workspace_service.domain.enums.ChannelRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "channel_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "channel_id", "user_id" })
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMemberJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelJpaEntity channel;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelRole role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

}