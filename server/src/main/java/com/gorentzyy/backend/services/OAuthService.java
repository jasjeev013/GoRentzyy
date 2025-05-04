package com.gorentzyy.backend.services;

import com.gorentzyy.backend.models.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface OAuthService {

    ResponseEntity<LoginResponse> handleGoogleCallback( String code);
}
