package com.project.workspace_service.shared; // Hoặc package chứa file này

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
public abstract class DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredOn;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
    }

    // Bắt buộc event con phải cho biết nó thuộc về ai
    public abstract UUID getAggregateId();

    public abstract String getAggregateType();
}