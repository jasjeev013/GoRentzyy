package com.gorentzyy.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${api.jwt.secret.value}")
    private String secretKey;

    @Getter
    @Value("${api.jwt.header}")
    private String jwtHeader;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String getAuthorities(String token){
        return (String) extractAllClaims(token).get("authorities");
    }

    public String extractUsername(String token) {
        return (String) extractAllClaims(token).get("username");
    }

    public String createToken(String email,String role){
        String jwt;
        jwt = Jwts.builder().issuer("GoRentzyy").subject("JWT Token")
            .claim("username",email)
            .claim("authorities",role)
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + 30000000))
            .signWith(getSigningKey()).compact();

        return jwt;
    }

}
