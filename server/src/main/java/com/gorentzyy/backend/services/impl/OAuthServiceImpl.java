package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.config.GoRentzyyUserDetailsService;
import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.OAuthService;
import com.gorentzyy.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private final RestTemplate restTemplate;
    private final GoRentzyyUserDetailsService goRentzyyUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public OAuthServiceImpl(RestTemplate restTemplate, GoRentzyyUserDetailsService goRentzyyUserDetailsService, PasswordEncoder passwordEncoder, UserRepository userRepository, JwtUtils jwtUtils) {
        this.restTemplate = restTemplate;
        this.goRentzyyUserDetailsService = goRentzyyUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;

    }
    @Override
    public ResponseEntity<LoginResponse> handleGoogleCallback(String code) {
        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
//            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("redirect_uri", "http://localhost:8080/api/google/callback");
            params.add("grant_type", "authorization_code");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            if (userInfoResponse.getStatusCode()== HttpStatus.OK){
                Map userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                String sub = (String) userInfo.get("sub");

                try {
                    goRentzyyUserDetailsService.loadUserByUsername(email);
                }catch (Exception e){
                    User user = new User();
                    user.setEmail(email);
                    user.setSocialLoginId(sub);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRole(AppConstants.Role.RENTER);
                    userRepository.save(user);
                }

                //Write JWT Code
                String jwt = jwtUtils.createToken(email,"ROLE_RENTER");


                return ResponseEntity.status(HttpStatus.OK)
                        .body(new LoginResponse("User successfully registered",jwt,"ROLE_RENTER"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("User Not registered","",""));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("Internal Server Error","",""));
        }
    }
}
