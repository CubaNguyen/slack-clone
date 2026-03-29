package com.project.workspace_service.domain.service;

public interface EmailService {
    void sendInvitationEmail(String toEmail, String token, String workspaceName);
}