package com.gorentzyy.backend.services.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceImplTests {

    private EmailServiceImpl emailService;


    public EmailServiceImplTests(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Test
    void testSendMail(){
        emailService.sendEmail("jasjeev99@gmail.com","Testing Chal rhi hai bhai ","Aur btaoo kaise hoo");
    }
}
