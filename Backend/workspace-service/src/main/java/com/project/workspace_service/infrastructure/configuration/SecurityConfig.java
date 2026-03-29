package com.project.workspace_service.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (Bắt buộc với API stateless)
                .csrf(csrf -> csrf.disable())

                // 2. Không lưu Session (Vì dùng Token/Header)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Phân quyền
                .authorizeHttpRequests(auth -> auth
                        // 👉 MỞ CỬA CHO SWAGGER (Quan trọng)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        // 👉 Cho phép mọi request khác đi qua
                        // (Vì ta sẽ tự check Header x-user-id trong SecurityUtils/Controller sau)
                        .anyRequest().permitAll());

        return http.build();
    }
}