package com.project.workspace_service.infrastructure.persistence.jdbc;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.domain.repository.ChannelMemberQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcChannelMemberRepository implements ChannelMemberQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM channel_members WHERE channel_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, channelId, userId));
    }

    @Override
    public long countAdminsByChannelId(UUID channelId) {
        String sql = """
                    SELECT COUNT(*)
                    FROM channel_members
                    WHERE channel_id = ? AND role = 'ADMIN'
                """; //

        // Sử dụng Types.OTHER để bảo vệ kiểu UUID trên Postgres
        return jdbcTemplate.queryForObject(
                sql,
                new Object[] { channelId },
                new int[] { java.sql.Types.OTHER },
                Long.class);
    }

}
