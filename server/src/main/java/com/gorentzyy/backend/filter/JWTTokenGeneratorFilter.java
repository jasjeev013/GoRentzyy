package com.gorentzyy.backend.filter;

import com.gorentzyy.backend.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;
@Component
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Autowired
    public JWTTokenGeneratorFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null!=authentication){
            String role = authentication.getAuthorities().stream().map(
                    GrantedAuthority::getAuthority
            ).collect(Collectors.joining(","));
            String jwt = jwtUtils.createToken(authentication.getName(),role);
            response.setHeader(jwtUtils.getJwtHeader(),jwt);
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !(request.getServletPath().equals("/api/user/login"));
    }
}
