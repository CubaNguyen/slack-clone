package com.project.workspace_service.shared;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}