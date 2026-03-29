package com.project.workspace_service.infrastructure.persistence.jdbc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.application.dto.BrowseChannelDto;
import com.project.workspace_service.application.dto.ChannelDto;
import com.project.workspace_service.application.dto.DefaultChannelDto;
import com.project.workspace_service.application.dto.SidebarChannelDto;
import com.project.workspace_service.domain.repository.ChannelQueryRepository; // Import Interface

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcChannelQueryRepository implements ChannelQueryRepository { // Implement Interface

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SidebarChannelDto> findMySidebarChannels(UUID workspaceId, UUID userId) {
        String sql = """
                    SELECT c.id, c.name, c.type, c.is_default, c.archived_at
                    FROM channels c
                    INNER JOIN channel_members cm ON c.id = cm.channel_id
                    WHERE c.workspace_id = ? AND cm.user_id = ? AND c.archived_at IS NULL
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new SidebarChannelDto(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("type"),
                rs.getBoolean("is_default"),
                rs.getTimestamp("archived_at") != null), workspaceId, userId);
    }

    @Override
    public List<BrowseChannelDto> findAllPublicChannels(UUID workspaceId, UUID userId) {
        String sql = """
                    SELECT c.id, c.name,
                           EXISTS (SELECT 1 FROM channel_members cm WHERE cm.channel_id = c.id AND cm.user_id = ?) as is_joined,
                           (SELECT COUNT(*) FROM channel_members cm WHERE cm.channel_id = c.id) as member_count
                    FROM channels c
                    WHERE c.workspace_id = ? AND c.type = 'PUBLIC' AND c.archived_at IS NULL
                    ORDER BY c.name ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new BrowseChannelDto(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getBoolean("is_joined"),
                rs.getInt("member_count")), userId, workspaceId);
    }

    @Override
    public Optional<ChannelDto> findById(UUID id) {
        String sql = """
                    SELECT id, name, type, is_default, archived_at, workspace_id
                    FROM channels
                    WHERE id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(new ChannelDto(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getBoolean("is_default"),
                        rs.getTimestamp("archived_at") != null,
                        UUID.fromString(rs.getString("workspace_id"))));
            }
            return Optional.empty();
        }, id);
    }

    // Vị trí: infrastructure/persistence/jdbc/JdbcChannelQueryRepository.java
    @Override
    public List<DefaultChannelDto> findDefaultChannelsByWorkspaceId(UUID workspaceId) {
        String sql = """
                    SELECT id, name, is_default
                    FROM channels
                    WHERE workspace_id = ? AND is_default = TRUE
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new DefaultChannelDto(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getBoolean("is_default")), workspaceId);
    }
}