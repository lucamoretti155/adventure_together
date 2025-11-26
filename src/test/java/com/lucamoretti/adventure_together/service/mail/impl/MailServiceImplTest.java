package com.lucamoretti.adventure_together.service.mail.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;   // ⭐ IMPORT NECESSARIO

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(emailService, "from", "noreply@test.com");
    }

    @Test
    void sendHtmlMessage_success() throws Exception {
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("template.html"), any(Context.class)))
                .thenReturn("<html>OK</html>");

        assertDoesNotThrow(() ->
                emailService.sendHtmlMessage(
                        "user@test.com",
                        "Test Subject",
                        "template.html",
                        Map.of("name", "Luca")
                )
        );

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlMessage_templateError_throws() {
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenThrow(new RuntimeException("template error"));

        assertThrows(RuntimeException.class, () ->
                emailService.sendHtmlMessage(
                        "user@test.com",
                        "Fail",
                        "template.html",
                        Map.of()
                )
        );
    }

    @Test
    void sendTestMail_success() {
        assertDoesNotThrow(() -> emailService.sendTestMail("user@test.com"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
