package com.project.workspace_service.infrastructure.persistence.jdbc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.application.dto.WorkspaceDto;
import com.project.workspace_service.domain.repository.WorkspaceQueryRepository; // Import Interface

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcWorkspaceQueryRepository implements WorkspaceQueryRepository { // Implement Interface

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<WorkspaceDto> findAllByUserId(UUID userId) {
        String sql = """
                    SELECT w.id, w.name, w.slug, wm.role,
                           (SELECT COUNT(*) FROM workspace_members WHERE workspace_id = w.id) as member_count
                    FROM workspaces w
                    INNER JOIN workspace_members wm ON w.id = wm.workspace_id
                    WHERE wm.user_id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new WorkspaceDto(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("slug"),
                rs.getString("role"),
                rs.getLong("member_count")), userId);
    }

    @Override
    public boolean isUserInWorkspace(UUID workspaceId, UUID userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM workspace_members WHERE workspace_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, workspaceId, userId));
    }

    @Override
    public boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM workspace_members
                    WHERE workspace_id = ? AND user_id = ?
                )
                """;

        // Khai báo kiểu dữ liệu UUID tường minh cho JDBC Driver
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        sql,
                        new Object[] { workspaceId, userId },
                        new int[] { java.sql.Types.OTHER, java.sql.Types.OTHER },
                        Boolean.class));
    }

    @Override
    public Optional<String> getNameById(UUID id) {
        String sql = "SELECT name FROM workspaces WHERE id = ?";
        try {
            String name = jdbcTemplate.queryForObject(
                    sql,
                    new Object[] { id },
                    new int[] { java.sql.Types.OTHER },
                    String.class);
            return Optional.ofNullable(name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}