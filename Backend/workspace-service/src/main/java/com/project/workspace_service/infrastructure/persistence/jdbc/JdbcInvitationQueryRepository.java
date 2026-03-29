package com.project.workspace_service.infrastructure.persistence.jdbc;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.workspace_service.domain.aggregate.invitation.Invitation;
import com.project.workspace_service.domain.enums.InvitationRole;
import com.project.workspace_service.domain.enums.InvitationStatus;
import com.project.workspace_service.domain.repository.InvitationQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcInvitationQueryRepository implements InvitationQueryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsByEmailAndWorkspaceIdAndStatus(String email, UUID workspaceId, String status) {
        // Sử dụng EXISTS để tối ưu hiệu năng
        String sql = """
                    SELECT EXISTS(
                        SELECT 1 FROM invitations
                        WHERE email = ? AND workspace_id = ? AND status = ?
                    )
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, email, workspaceId, status));
    }

    @Override
    public Optional<Invitation> findByToken(String token) {
        // Câu SQL lấy đầy đủ các trường cần thiết để tạo nên một Invitation Aggregate
        String sql = """
                    SELECT id, workspace_id, inviter_id, inviter_email, email, role, token, status, expires_at, created_at
                    FROM invitations
                    WHERE token = ?
                """;

        try {
            // queryForObject sẽ ném EmptyResultDataAccessException nếu không tìm thấy bản
            // ghi nào
            Invitation invitation = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                // Map từ ResultSet sang Domain Aggregate
                // Lưu ý: Dùng Builder của Invitation mà chúng ta đã sửa ở các bước trước
                return Invitation.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .workspaceId(UUID.fromString(rs.getString("workspace_id")))
                        .inviterId(UUID.fromString(rs.getString("inviter_id")))
                        .inviterEmail(rs.getString("inviter_email"))
                        .email(rs.getString("email"))
                        .role(InvitationRole.valueOf(rs.getString("role")))
                        .token(rs.getString("token"))
                        .status(InvitationStatus.valueOf(rs.getString("status")))
                        .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
            }, token);

            return Optional.ofNullable(invitation);
        } catch (EmptyResultDataAccessException e) {
            // Trả về Optional rỗng nếu không có token này trong DB
            return Optional.empty();
        }
    }

}