// package com.project.workspace_service.infrastructure.configuration;

// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.ApplicationListener;
// import org.springframework.core.env.Environment;
// import org.springframework.stereotype.Component;

// @Component
// @Slf4j
// public class ServerStartupListener implements ApplicationListener<ApplicationReadyEvent> {

//     @Override
//     public void onApplicationEvent(ApplicationReadyEvent event) {
//         Environment env = event.getApplicationContext().getEnvironment();

//         String protocol = "http";
//         String port = env.getProperty("server.port", "8080");
//         String contextPath = env.getProperty("server.servlet.context-path", "");
//         String swaggerPath = env.getProperty("springdoc.swagger-ui.path", "/swagger-ui/index.html");
//         String appName = env.getProperty("spring.application.name");

//         // Dùng mã màu ANSI chuẩn
//         final String RESET = "\u001B[0m";
//         final String YELLOW = "\u001B[33m"; // Màu vàng cho khung
//         final String GREEN = "\u001B[32;1m"; // Màu xanh lá đậm cho tên App
//         final String CYAN = "\u001B[36m"; // Màu xanh ngọc cho tiêu đề
//         final String BLUE_LINK = "\u001B[34;4m"; // Xanh dương gạch chân cho Link

//         log.info("\n" +
//                 YELLOW + "============================================================" + RESET + "\n" +
//                 YELLOW + "| " + RESET + "Application: " + GREEN + String.format("%-43s", appName) + RESET + YELLOW + "|"
//                 + RESET + "\n" +
//                 YELLOW + "| " + RESET + "Swagger UI : " + BLUE_LINK + "{}://localhost:{}{}{}" + RESET + "    " + YELLOW
//                 + "|" + RESET + "\n" +
//                 YELLOW + "============================================================" + RESET,
//                 protocol, port, contextPath, swaggerPath);
//     }
// }

package com.project.workspace_service.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();

        String protocol = "http";
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        // Lấy thêm cấu hình servlet path (/api/v1)
        String servletPath = env.getProperty("spring.mvc.servlet.path", "");
        String appName = env.getProperty("spring.application.name");

        // Mặc định springdoc trỏ vào /swagger-ui/index.html
        String swaggerPath = env.getProperty("springdoc.swagger-ui.path", "/swagger-ui/index.html");

        // Xử lý ghép chuỗi URL để tránh bị thừa dấu / (ví dụ //api/v1)
        String baseUrl = (contextPath + servletPath).replace("//", "/");
        // Nếu baseUrl rỗng hoặc chỉ có "/" thì xử lý để in ra đẹp
        if (baseUrl.equals("/"))
            baseUrl = "";

        // Dùng mã màu ANSI chuẩn
        final String RESET = "\u001B[0m";
        final String YELLOW = "\u001B[33m";
        final String GREEN = "\u001B[32;1m";
        final String CYAN = "\u001B[36m";
        final String BLUE_LINK = "\u001B[34;4m";

        log.info("\n" +
                YELLOW + "============================================================" + RESET + "\n" +
                YELLOW + "| " + RESET + "Application: " + GREEN + String.format("%-43s", appName) + RESET + YELLOW + "|"
                + RESET + "\n" +
                YELLOW + "| " + RESET + "Swagger UI : " + BLUE_LINK + "{}://localhost:{}{}{}" + RESET + "    " + YELLOW
                + "|" + RESET + "\n" +
                YELLOW + "============================================================" + RESET,
                protocol, port, baseUrl, swaggerPath);
    }
}