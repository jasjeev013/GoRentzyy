package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.services.OAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<LoginResponse> handleGoogleCallback(@RequestParam String code){
        return oAuthService.handleGoogleCallback(code);
    }
 }

