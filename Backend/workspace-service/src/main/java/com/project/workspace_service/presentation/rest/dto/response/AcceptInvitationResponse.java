package com.project.workspace_service.presentation.rest.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcceptInvitationResponse {
    private UUID workspaceId;
    private String message;
}