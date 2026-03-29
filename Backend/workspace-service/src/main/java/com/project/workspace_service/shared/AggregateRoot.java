package com.project.workspace_service.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {

    // List chứa các sự kiện tạm thời (chưa bắn đi)
    // @JsonIgnore để khi lưu Entity xuống DB (dạng JSON nếu có) hoặc trả về API, nó
    // không kèm theo list này
    @JsonIgnore
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // Hàm này cho phép Entity con (như Workspace) thêm sự kiện vào
    // protected: Chỉ cho phép class con gọi
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    // Hàm này để Service lấy danh sách sự kiện ra để bắn (publish)
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    // Hàm này để xóa sự kiện sau khi đã bắn xong (tránh bắn lại 2 lần)
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}