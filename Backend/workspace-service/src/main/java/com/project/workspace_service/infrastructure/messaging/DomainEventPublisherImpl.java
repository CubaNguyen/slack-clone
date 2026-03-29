package com.project.workspace_service.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.workspace_service.infrastructure.outbox.OutboxEventJpaEntity;
import com.project.workspace_service.infrastructure.outbox.OutboxRepository;
import com.project.workspace_service.shared.DomainEvent;
import com.project.workspace_service.shared.DomainEventPublisher;
import com.project.workspace_service.shared.event.EventEnvelope;
import com.project.workspace_service.shared.utils.SecurityUtils;

import io.micrometer.tracing.Tracer;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DomainEventPublisherImpl implements DomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper; // Spring tự inject cái có sẵn
    private final Tracer tracer;
    private final ApplicationEventPublisher springEventPublisher;

    public DomainEventPublisherImpl(OutboxRepository outboxRepository, ObjectMapper objectMapper, Tracer tracer,
            ApplicationEventPublisher springEventPublisher) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.tracer = tracer;
        this.springEventPublisher = springEventPublisher;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    // MANDATORY: Bắt buộc phải chạy chung transaction với thằng gọi nó
    // (CreateWorkspaceHandler)
    // Nếu thằng kia fail, thì event này cũng không được lưu (Atomic).
    public void publish(DomainEvent event) {
        try {
            String traceId = "";
            if (tracer.currentSpan() != null) {
                traceId = tracer.currentSpan().context().traceId();
            } else {
                // Fallback nếu hệ thống tracing chưa khởi tạo kịp (hiếm gặp)
                traceId = UUID.randomUUID().toString();
            }

            UUID actorId = SecurityUtils.getCurrentUserId();
            // Fallback: Nếu API này là public hoặc được gọi bởi hệ thống (System) không có
            // user đăng nhập
            if (actorId == null) {
                actorId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }

            EventEnvelope envelope = new EventEnvelope(
                    new EventEnvelope.Meta(
                            event.getEventId(),
                            traceId,
                            new EventEnvelope.Actor(actorId, "USER"),
                            event.getOccurredOn()),
                    event // Đây là phần "data"
            );

            String payload = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(envelope);

            OutboxEventJpaEntity outboxEntity = OutboxEventJpaEntity.builder()
                    .id(event.getEventId())
                    .aggregateId(event.getAggregateId()) // Lấy từ Abstract Class
                    .aggregateType(event.getAggregateType()) // Lấy từ Abstract Class
                    .eventType(event.getClass().getSimpleName()) // Ví dụ: "WorkspaceCreatedEvent"
                    .payload(payload)
                    .createdAt(event.getOccurredOn())
                    .build();

            outboxRepository.save(outboxEntity);

            springEventPublisher.publishEvent(event);

        } catch (JsonProcessingException e) {
            // Log lỗi nghiêm trọng, vì không parse được event là toang
            throw new RuntimeException("Error serializing domain event", e);
        }
    }
}