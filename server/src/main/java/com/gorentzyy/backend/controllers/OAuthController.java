package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.services.OAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/google")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping("/callback")
    public ResponseEntity<LoginResponse> handleGoogleCallback(@RequestBody Map<String, String> body){
        String code = body.get("code");
        return oAuthService.handleGoogleCallback(code);
    }
 }

