package com.project.workspace_service.infrastructure.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.project.workspace_service.domain.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendInvitationEmail(String toEmail, String token, String workspaceName) {
        // Nếu ông đang test localhost mà không muốn gửi mail thật thì uncomment dòng
        // dưới
        // System.out.println("Gửi mail ảo tới: " + toEmail + " | Link: " + token);
        // return;

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Lời mời tham gia Workspace: " + workspaceName);

            // Nội dung HTML đơn giản
            String htmlContent = String.format("""
                    <html>
                    <body>
                        <h2>Bạn nhận được lời mời tham gia %s</h2>
                        <p>Click vào link bên dưới để chấp nhận:</p>
                        <a href="http://localhost:3000/invite/accept?token=%s">Chấp nhận lời mời</a>
                        <p>Link hết hạn sau 7 ngày.</p>
                    </body>
                    </html>
                    """, workspaceName, token);

            helper.setText(htmlContent, true); // true = html

            javaMailSender.send(message);

        } catch (MessagingException e) {
            // Quan trọng: Ném RuntimeException để kích hoạt Rollback ở lớp Handler
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage());
        }
    }
}