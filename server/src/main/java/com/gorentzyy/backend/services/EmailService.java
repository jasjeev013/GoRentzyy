package com.gorentzyy.backend.services;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    public void sendEmail(String to,String subject,String body);
    CompletableFuture<Void> sendEmailAsync(String to, String subject, String body);
    void sendEmailToMultipleRecipients(String[] to, String subject, String body);

}
