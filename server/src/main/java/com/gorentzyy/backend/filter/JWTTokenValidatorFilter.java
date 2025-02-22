package com.gorentzyy.backend.filter;

import com.gorentzyy.backend.constants.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(AppConstants.JWT_HEADER);
        if (null != jwt){
            try {
                Environment env = getEnvironment();
                if (null!=env) {
                    String secret = env.getProperty(AppConstants.JWT_SECRET_KEY, AppConstants.JWT_SECRET_DEFAULT_VALUE);
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    if (null != secretKey){
                        Claims claims = Jwts.parser().verifyWith(secretKey)
                                .build().parseSignedClaims(jwt).getPayload();

                        // 🔥 Ensure token is not expired
                        if (claims.getExpiration().before(new Date())) {
                            throw new BadCredentialsException("Token expired!!");
                        }
                        System.out.println(claims.get("authorities") + " Validator");
                        // ✅ Fix role prefix issue
                        String authorities = Arrays.stream(String.valueOf(claims.get("authorities")).split(","))
                                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                .collect(Collectors.joining(","));
                        System.out.println(authorities);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                claims.get("username"),
                                null,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                        );
                        System.out.println(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }catch (Exception ex){
                throw new BadCredentialsException("Invalid Token Received!!");
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return (request.getServletPath().equals("/api/user/login") && "POST".equalsIgnoreCase(request.getMethod())) || request.getServletPath().equals("/api/user/basicAuth/login");
    }

}
