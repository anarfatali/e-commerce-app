package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Async
    public void sendVerificationCodeEmail(String to, String code) {
        String subject = "Verify your email address";
        String body = """
                %s

                Your verification code is: %s

                This code expires in 10 minutes.
                """.formatted(appName, code);

        sendText(to, subject, body);
    }

    @Override
    @Async
    public void sendPasswordResetCodeEmail(String to, String code) {
        String subject = "Reset your password";
        String body = """
                %s

                Your password reset code is: %s

                This code expires in 10 minutes.
                If you didn't request this, ignore this email.
                """.formatted(appName, code);

        sendText(to, subject, body);
    }

    private void sendText(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
            log.info("[EMAIL] Sent '{}' to {}", subject, to);
        } catch (MessagingException e) {
            log.error("[EMAIL] Failed to send '{}' to {}: {}", subject, to, e.getMessage());
        }
    }
}