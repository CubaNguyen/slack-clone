package com.project.workspace_service.application.command.createworkspace;

import java.util.UUID;

// Interface bạn tự định nghĩa hoặc dùng ApplicationEventPublisher của Spring
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.domain.aggregate.workspace.Workspace;
import com.project.workspace_service.domain.repository.WorkspaceRepository;
import com.project.workspace_service.shared.DomainEventPublisher;
import com.project.workspace_service.shared.enums.ErrorCode;
import com.project.workspace_service.shared.exception.AppException;

@Service
public class CreateWorkspaceHandler {

    private final WorkspaceRepository workspaceRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateWorkspaceHandler(WorkspaceRepository workspaceRepository, DomainEventPublisher eventPublisher) {
        this.workspaceRepository = workspaceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID handle(CreateWorkspaceCommand command) {
        // 1. Kiểm tra slug unique (Domain Service hoặc check nhanh ở đây)
        if (workspaceRepository.existsBySlug(command.slug())) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS,
                    "Slug '" + command.slug() + "' đã có người dùng rồi!");
        }

        // 2. Gọi Domain để tạo Workspace
        Workspace workspace = Workspace.create(command.name(), command.slug(), command.ownerId());

        // 3. Lưu vào DB
        workspaceRepository.save(workspace);

        // 4. Phát tán sự kiện (Để kích hoạt Saga step 2 & 3)
        // Lưu ý: Các event được add trong aggregate sẽ được lấy ra và gửi đi tại đây
        workspace.getDomainEvents().forEach(eventPublisher::publish);
        workspace.clearDomainEvents();

        return workspace.getId();
    }
}