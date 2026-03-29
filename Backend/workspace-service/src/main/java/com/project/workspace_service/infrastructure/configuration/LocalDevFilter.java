package com.project.workspace_service.infrastructure.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@Component
@Profile("dev") // <--- QUAN TRỌNG: Chỉ chạy khi profile là "dev"
public class LocalDevFilter implements Filter {

    // ID giả lập để test (User ID hardcode mà bạn muốn)
    private static final String MOCK_USER_ID = "78cc219b-36ab-4ac6-a4af-eebf50c397c9";
    private static final String MOCK_USER_EMAIL = "cuba300304@gmail.com";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Nếu request chưa có header x-user-id (tức là test bằng Swagger/Postman mà
        // lười điền)
        // Thì ta tự động thêm vào.
        if (httpRequest.getHeader("x-user-id") == null) {
            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpRequest);
            requestWrapper.addHeader("x-user-id", MOCK_USER_ID);
            requestWrapper.addHeader("x-user-email", MOCK_USER_EMAIL); // 2. Nhét Email vào đây
            chain.doFilter(requestWrapper, response);
        } else {
            // Nếu đã có (tự truyền tay test user khác), thì cứ để nguyên
            chain.doFilter(request, response);
        }
    }

    // Class con để ghi đè Header (Vì mặc định Header của Request là Read-Only)
    public static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> headerMap = new HashMap<>();

        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = super.getHeader(name);
            if (headerMap.containsKey(name)) {
                headerValue = headerMap.get(name);
            }
            return headerValue;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = new ArrayList<>();
            if (super.getHeaderNames() != null) {
                names.addAll(Collections.list(super.getHeaderNames()));
            }
            names.addAll(headerMap.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = new ArrayList<>();
            if (headerMap.containsKey(name)) {
                values.add(headerMap.get(name));
            } else if (super.getHeaders(name) != null) {
                values.addAll(Collections.list(super.getHeaders(name)));
            }
            return Collections.enumeration(values);
        }
    }
}