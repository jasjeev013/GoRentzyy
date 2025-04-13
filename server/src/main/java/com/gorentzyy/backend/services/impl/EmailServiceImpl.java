package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            System.out.println("Hello jiii");
            SimpleMailMessage mail  =new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

//            javaMailSender.send(mail);
        }catch (Exception e){
            log.error("Exception while sendEmail ",e);
        }
    }
}
