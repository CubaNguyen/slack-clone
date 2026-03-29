package com.project.workspace_service.infrastructure.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Tự động tạo topic "workspace-events" nếu chưa có
    @Bean
    public NewTopic workspaceEventsTopic() {
        return TopicBuilder.name("workspace-events")
                .partitions(3) // Chia 3 luồng xử lý cho nhanh
                .replicas(1)
                .build();
    }
}