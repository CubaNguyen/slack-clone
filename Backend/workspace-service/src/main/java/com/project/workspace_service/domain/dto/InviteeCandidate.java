package com.project.workspace_service.domain.dto;

import java.util.UUID;

public record InviteeCandidate(
        UUID id,
        String email) {
}