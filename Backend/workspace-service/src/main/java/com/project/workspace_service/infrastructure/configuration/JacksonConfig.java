package com.project.workspace_service.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Quan trọng: Đăng ký module xử lý ngày giờ Java 8+ (LocalDateTime)
        mapper.registerModule(new JavaTimeModule());

        // Quan trọng: Tắt tính năng viết ngày giờ dưới dạng số (Timestamp)
        // Để nó ra chuỗi ISO-8601 dễ đọc ("2026-01-21T...")
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}