package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.OAuthAuthenticationException;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.OAuthService;
import com.gorentzyy.backend.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private static final String GOOGLE_TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_ENDPOINT = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public OAuthServiceImpl(RestTemplate restTemplate,
                            PasswordEncoder passwordEncoder,
                            UserRepository userRepository,
                            JwtUtils jwtUtils) {
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<LoginResponse> handleGoogleCallback(String code) {
        log.info("Handling Google OAuth callback with authorization code");

        try {
            // Exchange authorization code for tokens
            String idToken = exchangeCodeForTokens(code);

            // Get user info from Google
            Map<String, Object> userInfo = getUserInfoFromGoogle(idToken);

            // Process user authentication
            User user = processOAuthUser(userInfo);

            // Generate JWT token
            String jwtToken = generateJwtToken(user);

            log.info("Successful Google OAuth authentication for user: {}", user.getEmail());
            return buildSuccessResponse(user, jwtToken);

        } catch (OAuthAuthenticationException e) {
            log.error("Google OAuth authentication failed: {}", e.getMessage());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during Google OAuth authentication", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private String exchangeCodeForTokens(String code) {
        log.debug("Exchanging authorization code for tokens");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", frontendUrl + "/api/auth/callback/google");
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GOOGLE_TOKEN_ENDPOINT, request, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new OAuthAuthenticationException("Failed to exchange code for tokens");
            } else {
                response.getBody();
            }

            String idToken = (String) response.getBody().get("id_token");
            if (idToken == null) {
                throw new OAuthAuthenticationException("Missing ID token in Google response");
            }

            return idToken;

        } catch (RestClientException e) {
            throw new OAuthAuthenticationException("Error communicating with Google OAuth endpoint");
        }
    }

    private Map<String, Object> getUserInfoFromGoogle(String idToken) {
        log.debug("Retrieving user info from Google with ID token");

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    GOOGLE_USERINFO_ENDPOINT + idToken, Map.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new OAuthAuthenticationException("Failed to retrieve user info from Google");
            } else {
                response.getBody();
            }

            return response.getBody();

        } catch (RestClientException e) {
            throw new OAuthAuthenticationException("Error communicating with Google userinfo endpoint");
        }
    }

    private User processOAuthUser(Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String sub = (String) userInfo.get("sub");

        if (email == null || sub == null) {
            throw new OAuthAuthenticationException("Invalid user info from Google");
        }

        log.debug("Processing OAuth user with email: {}", email);

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            log.debug("Found existing user for email: {}", email);
            return existingUser.get();
        }

        // Create new user for first-time OAuth login
        log.info("Creating new user for OAuth login with email: {}", email);
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(name != null ? name : "User-" + UUID.randomUUID().toString().substring(0, 8));
        newUser.setSocialLoginId(sub);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRole(AppConstants.Role.RENTER);
        newUser.setEmailVerified(true);

        try {
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new OAuthAuthenticationException("Failed to create new user account");
        }
    }

    private String generateJwtToken(User user) {
        return jwtUtils.createToken(user.getEmail(), "ROLE_" + user.getRole().name());
    }

    private ResponseEntity<LoginResponse> buildSuccessResponse(User user, String jwtToken) {
        return ResponseEntity.ok()
                .body(new LoginResponse(
                        "Authentication successful",
                        jwtToken,
                        user.getRole().name(),
                        true
                ));
    }

    private ResponseEntity<LoginResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new LoginResponse(
                        message,
                        "",
                        "",
                        false
                ));
    }
}