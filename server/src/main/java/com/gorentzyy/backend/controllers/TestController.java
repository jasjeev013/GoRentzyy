package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final EmailService emailService;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponseObject> test(){
        return new ResponseEntity<>(new ApiResponseObject("THe API is running fine",true,null), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponseObject> testPost(@RequestBody String name){
        return new ResponseEntity<>(new ApiResponseObject("THe API is running fine",true,name),HttpStatus.ACCEPTED);
    }

    @PostMapping("/email")
    public ResponseEntity<ApiResponseObject> testEmail(){
        emailService.sendEmail("jasjeev99@gmail.com","Aur bhai kaisa hai ","THis is an auto generated mail");
        return new ResponseEntity<>(new ApiResponseObject("THe API is running fine",true,null),HttpStatus.ACCEPTED);
    }

}
