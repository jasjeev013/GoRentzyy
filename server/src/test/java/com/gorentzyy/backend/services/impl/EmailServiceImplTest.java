package com.gorentzyy.backend.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendEmail_WhenValidInput_ShouldSendEmailSuccessfully() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Email Body";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WhenMailSenderFails_ShouldLogError() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Email Body";

        doThrow(new RuntimeException("Mail server error"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        // Error should be logged (can verify logs if needed)
    }

    @Test
    void sendEmail_ShouldSetCorrectMailProperties() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Email Body";

        // Capture the mail message to verify its properties
        SimpleMailMessage[] capturedMessage = new SimpleMailMessage[1];
        doAnswer(invocation -> {
            capturedMessage[0] = invocation.getArgument(0);
            return null;
        }).when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        assertEquals(to, capturedMessage[0].getTo()[0]);
        assertEquals(subject, capturedMessage[0].getSubject());
        assertEquals(body, capturedMessage[0].getText());
    }
}