package com.project.workspace_service.domain.gateway;

import com.project.workspace_service.domain.dto.InviteeCandidate;

// Interface này nằm ở Domain
public interface UserGateway {
    // Hàm này trả về cái gì?
    // Nếu ExternalUser nằm ở Infra -> Domain phải "import infrastructure..."
    // => VI PHẠM NGUYÊN TẮC QUAN TRỌNG NHẤT!
    InviteeCandidate getUserByEmail(String email);
}