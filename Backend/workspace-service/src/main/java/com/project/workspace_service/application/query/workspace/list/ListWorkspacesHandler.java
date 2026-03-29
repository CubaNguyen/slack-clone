package com.project.workspace_service.application.query.workspace.list;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.workspace_service.application.dto.WorkspaceDto;
import com.project.workspace_service.domain.repository.WorkspaceQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListWorkspacesHandler {

    private final WorkspaceQueryRepository queryRepository;

    public List<WorkspaceDto> handle(ListWorkspacesQuery query) {
        // [Suy luận] Chỗ này sau này có thể thêm Logic Caching với Redis ở đây
        return queryRepository.findAllByUserId(query.userId());
    }
}