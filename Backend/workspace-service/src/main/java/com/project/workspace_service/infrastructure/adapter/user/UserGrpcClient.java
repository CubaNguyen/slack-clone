package com.project.workspace_service.infrastructure.adapter.user;

import org.springframework.stereotype.Component;

import com.project.user.grpc.GetUserForInviteRequest;
import com.project.user.grpc.UserInviteInfo;
import com.project.user.grpc.UserServiceGrpc;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Component
public class UserGrpcClient {

    // "user-service" là tên cấu hình trong application.yml
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    public UserInviteInfo getUserByEmail(String email) {
        GetUserForInviteRequest request = GetUserForInviteRequest.newBuilder()
                .setEmail(email)
                .build();

        // Gọi bắn sang User Service
        return userStub.getUserForInvite(request);
    }
}