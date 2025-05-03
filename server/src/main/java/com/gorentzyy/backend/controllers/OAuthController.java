package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.config.GoRentzyyUserDetailsService;
import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.constants.SecretConstants;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/google")
public class OAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private RestTemplate restTemplate;

    private GoRentzyyUserDetailsService goRentzyyUserDetailsService;


    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    private final Environment env;

    public OAuthController(RestTemplate restTemplate, GoRentzyyUserDetailsService goRentzyyUserDetailsService, PasswordEncoder passwordEncoder, UserRepository userRepository, Environment env) {
        this.restTemplate = restTemplate;
        this.goRentzyyUserDetailsService = goRentzyyUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.env = env;
    }

    @GetMapping("/callback")
    public ResponseEntity<LoginResponse> handleGoogleCallback(@RequestParam String code){
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
                Map<String,Object> userInfo = userInfoResponse.getBody();
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
                String secret = env.getProperty(SecretConstants.JWT_SECRET_KEY, SecretConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                String jwt = Jwts.builder().issuer("GoRentzyy").subject("JWT Token")
                        .claim("username",email)
                        .claim("authorities","ROLE_RENTER")
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();


                return ResponseEntity.status(HttpStatus.OK)
                        .body(new LoginResponse("User successfully registered",jwt));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("User Not registered",""));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("Internal Server Error",""));
        }
    }
 }

/*

https://developers.google.com/oauthplayground/#step1
&scopes=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email
&url=https%3A%2F%2F&content_type=application%2Fjson
&http_method=GET
&useDefaultOauthCred=checked&oauthEndpointSelect=Google
&oauthAuthEndpointValue=https%3A%2F%2Faccounts.google.com%2Fo%2Foauth2%2Fv2%2Fauth
&oauthTokenEndpointValue=https%3A%2F%2Foauth2.googleapis.com%2Ftoken
&oauthClientId=651678101300-s8ae5sk9i4plqkvpmodag85gn3g1j1ek.apps.googleusercontent.com
&oauthClientSecret=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&includeCredentials=checked
&accessTokenType=bearer
&autoRefreshToken=unchecked&accessType=offline
&prompt=consent
&response_type=token
&wrapLines=on


Frontend will hit this api key for the authorization code
https://accounts.google.com/o/oauth2/auth?
client_id=YOUR_CLIENT_ID
    &redirect_uri=YOUR_REDIRECT_URI (/api/google/callback)
    &response_type=code
    &scope=email profile (https://www.googleapis.com/auth/userinfo.email)
    &access_type=offline
    &prompt=consent


https://accounts.google.com/o/oauth2/auth?
client_id=651678101300-s8ae5sk9i4plqkvpmodag85gn3g1j1ek.apps.googleusercontent.com
    &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fgoogle%2Fcallback
    &response_type=code
    &scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email
    &access_type=offline
    &prompt=consent


https://accounts.google.com/o/oauth2/v2/auth?
    &client_id=651678101300-s8ae5sk9i4plqkvpmodag85gn3g1j1ek.apps.googleusercontent.com
    redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground
    &response_type=code
    &scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email
    &access_type=offline
    &prompt=consent

https://developers.google.com/oauthplayground
https://www.googleapis.com/auth/userinfo.email

GET /oauthplayground/?
    code=4%2F0Ab_5qlnlXb5UCEoQBATSppDii4fcN6VvM5biJ9k7ZRDeRjL5qjvKQdk6AXKjXww_3k5rBQ
    &scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=0
    &prompt=consent HTTP/1.1
Host: developers.google.com

*/