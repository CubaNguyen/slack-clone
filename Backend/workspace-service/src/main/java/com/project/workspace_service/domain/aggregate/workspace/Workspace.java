package com.project.workspace_service.domain.aggregate.workspace;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import com.project.workspace_service.domain.event.WorkspaceCreatedEvent;
import com.project.workspace_service.shared.AggregateRoot;

public class Workspace extends AggregateRoot {
    private UUID id;
    private String name;
    private String slug;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private WorkspaceSettings settings;
    // ... các field khác

    // Constructor private để ép buộc dùng factory method
    private Workspace(UUID id, String name, String slug, UUID ownerId) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.createdBy = ownerId;
        this.createdAt = LocalDateTime.now();
    }

    // Factory method: Nơi khởi tạo và validate logic
    public static Workspace create(String name, String slug, UUID ownerId) {
        // 1. Validate Business Rules
        if (slug == null || slug.isEmpty()) {
            throw new IllegalArgumentException("Slug không được để trống");
        }

        // Slug chỉ được chứa chữ thường, số và gạch ngang
        if (!Pattern.matches("^[a-z0-9-]+$", slug)) {
            throw new IllegalArgumentException("Slug chỉ chấp nhận chữ thường, số và gạch ngang (ví dụ: backend-team)");
        }

        // 2. Tạo entity
        UUID workspaceId = UUID.randomUUID();
        Workspace workspace = new Workspace(workspaceId, name, slug, ownerId);
        workspace.settings = WorkspaceSettings.createDefault(workspace.getId());
        // 3. Đăng ký Domain Event (Bước quan trọng cho Saga)
        // Sự kiện này báo hiệu: "Workspace đã tạo xong, ai muốn làm gì tiếp theo thì
        // làm đi"
        workspace.addDomainEvent(new WorkspaceCreatedEvent(workspaceId, ownerId));

        return workspace;
    }

    public static Workspace restore(UUID id, String name, String slug, UUID ownerId) {
        // Gọi constructor private
        Workspace workspace = new Workspace(id, name, slug, ownerId);

        // Lưu ý: restore không gọi addDomainEvent() vì đây là dữ liệu cũ đã tồn tại
        return workspace;
    }

    // Getters...
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public WorkspaceSettings getSettings() {
        return settings;
    }

    public void setSettings(WorkspaceSettings settings) {
        this.settings = settings;
    }
}