package com.project.workspace_service.infrastructure.adapter.user;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.project.user.grpc.UserInviteInfo;
import com.project.workspace_service.domain.dto.InviteeCandidate;
import com.project.workspace_service.domain.gateway.UserGateway;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {

    private final RedisTemplate<String, InviteeCandidate> redisTemplate;
    private final UserGrpcClient grpcClient;

    private static final String REDIS_KEY_PREFIX = "user:email:";

    @Override
    public InviteeCandidate getUserByEmail(String email) {
        String key = REDIS_KEY_PREFIX + email;

        // 1. Check Redis (Cache-Aside Pattern)
        try {
            InviteeCandidate cachedUser = (InviteeCandidate) redisTemplate.opsForValue().get(key);
            if (cachedUser != null) {
                log.info("🎯 Cache HIT for email: {}", email);
                return cachedUser;
            }
        } catch (Exception e) {
            log.error("Redis error: {}", e.getMessage()); // Redis chết thì kệ nó, đi tiếp
        }

        // 2. Cache MISS -> Gọi gRPC
        log.info("Cache MISS. Calling gRPC for email: {}", email);
        try {
            UserInviteInfo grpcResponse = grpcClient.getUserByEmail(email);
            // Kiểm tra nếu gRPC trả về trống hoặc không tìm thấy user
            if (grpcResponse == null || grpcResponse.getId().isEmpty()) {
                System.err.println("🎯________________ Invitee User ID: null");
                return null; // Trả về null để Handler ném lỗi USER_NOT_FOUND
            }
            // 3. Map từ gRPC Response sang Domain DTO
            InviteeCandidate user = new InviteeCandidate(
                    UUID.fromString(grpcResponse.getId()),
                    grpcResponse.getEmail());

            // 4. Lưu lại vào Redis (TTL 1 giờ)
            try {
                redisTemplate.opsForValue().set(key, user, Duration.ofHours(1));
            } catch (Exception e) {
                log.error("Failed to save to Redis", e);
            }

            return user;

        } catch (io.grpc.StatusRuntimeException e) {
            log.error("gRPC service unavailable: {}", e.getStatus());
            // KHÔNG return null ở đây, phải ném lỗi hệ thống
            throw new AppException(ErrorCode.USER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Unexpected error in UserGateway: {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Implement thêm getUserById tương tự...
}