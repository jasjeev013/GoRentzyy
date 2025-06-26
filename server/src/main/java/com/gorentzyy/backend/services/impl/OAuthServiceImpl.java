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

    @Value("${app.frontend-url}")
    private String frontendUrl;
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
            params.add("redirect_uri", frontendUrl + "/api/auth/callback/google");
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("Failed to authenticate with Google", "", ""));
            }
            String idToken = (String) tokenResponse.getBody().get("id_token");
            String accessToken = (String) tokenResponse.getBody().get("access_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            if (userInfoResponse.getStatusCode()== HttpStatus.OK){
                Map userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                String name = UUID.randomUUID().toString();
                String sub = (String) userInfo.get("sub");
                User user;
                if (userRepository.findByEmail(email).isPresent()) {
                    user = userRepository.findByEmail(email).get();
                }else{
                    user = new User();
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setSocialLoginId(sub);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRole(AppConstants.Role.RENTER);
                    user.setEmailVerified(true); // Google verified emails
                    userRepository.save(user);
                }

                //Write JWT Code
                String jwt = jwtUtils.createToken(email,"ROLE_" + user.getRole().name());


                return ResponseEntity.ok()
                        .body(new LoginResponse("Authentication successful", jwt, user.getRole().name()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Failed to verify Google user", "", ""));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("Internal Server Error: " + e.getMessage(), "", ""));
        }
    }
}
