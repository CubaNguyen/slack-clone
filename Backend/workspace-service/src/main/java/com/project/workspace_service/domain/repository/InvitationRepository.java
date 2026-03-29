package com.project.workspace_service.domain.repository;

import com.project.workspace_service.domain.aggregate.invitation.Invitation;

public interface InvitationRepository {
    void save(Invitation invitation); // Dùng cho Command side
}
