package com.project.workspace_service.infrastructure.outbox.worker;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.workspace_service.infrastructure.persistence.jpa.repository.OutboxEventJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxWorker {

    private final OutboxEventJpaRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "workspace-events";

    // Chạy mỗi 2 giây (2000ms)
    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOutboxEvents() {
        // 1. Tìm các event chưa gửi (processedAt = null)
        var events = outboxEventRepository.findByProcessedAtIsNullOrderByCreatedAtAsc();
        System.err.println("OutboxWorker found " + events);
        if (events.isEmpty())
            return;

        log.debug("Found {} events to send", events.size());

        for (var event : events) {
            try {
                // 2. Gửi Kafka
                // Key = ID đối tượng (để đảm bảo thứ tự), Value = JSON Payload
                kafkaTemplate.send(TOPIC, event.getAggregateId().toString(), event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("🚀 Sent to Kafka: EventID={}", event.getId());
                            } else {
                                log.error("🔥 Send failed: {}", ex.getMessage());
                            }
                        });

                // 3. Đánh dấu đã gửi
                event.setProcessedAt(LocalDateTime.now());
                outboxEventRepository.save(event);

            } catch (Exception e) {
                log.error("Error processing outbox event: {}", event.getId(), e);
            }
        }
    }
}