package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.EmailSendingException;
import com.gorentzyy.backend.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
                            @Value("${spring.mail.username}") String fromEmail) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    @Async
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body) {
        return CompletableFuture.runAsync(() -> sendEmail(to, subject, body));
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        validateEmailParameters(to, subject, body);

        try {
            SimpleMailMessage mailMessage = createMailMessage(to, subject, body);
            log.debug("Attempting to send email to: {}, subject: {}", to, subject);

            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to: {}, subject: {}", to, subject);

        } catch (MailException e) {
            String errorMsg = String.format("Failed to send email to %s. Subject: %s ", to, subject);
            log.error(errorMsg, e);
            throw new EmailSendingException(errorMsg+e);
        } catch (Exception e) {
            String errorMsg = String.format("Unexpected error sending email to %s ", to);
            log.error(errorMsg, e);
            throw new EmailSendingException(errorMsg+ e);
        }
    }

    @Override
    public void sendEmailToMultipleRecipients(String[] to, String subject, String body) {
        validateEmailParameters(to, subject, body);

        try {
            SimpleMailMessage mailMessage = createMailMessage(to, subject, body);
            log.debug("Attempting to send email to {} recipients, subject: {}", to.length, subject);

            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to {} recipients, subject: {}", to.length, subject);

        } catch (MailException e) {
            String errorMsg = String.format("Failed to send email to multiple recipients. Subject: %s ", subject);
            log.error(errorMsg, e);
            throw new EmailSendingException(errorMsg+ e);
        }
    }

    private SimpleMailMessage createMailMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    private SimpleMailMessage createMailMessage(String[] to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    private void validateEmailParameters(String to, String subject, String body) {
        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("Recipient email cannot be null or empty");
        }
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("Email subject cannot be null or empty");
        }
        if (!StringUtils.hasText(body)) {
            throw new IllegalArgumentException("Email body cannot be null or empty");
        }
    }

    private void validateEmailParameters(String[] to, String subject, String body) {
        if (to == null || to.length == 0) {
            throw new IllegalArgumentException("Recipient emails array cannot be null or empty");
        }
        for (String recipient : to) {
            if (!StringUtils.hasText(recipient)) {
                throw new IllegalArgumentException("Recipient email in array cannot be null or empty");
            }
        }
        validateEmailParameters("dummy@example.com", subject, body); // Validate subject and body
    }
}