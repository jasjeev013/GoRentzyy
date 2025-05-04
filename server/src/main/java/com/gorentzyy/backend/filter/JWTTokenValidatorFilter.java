package com.gorentzyy.backend.filter;

import com.gorentzyy.backend.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    @Autowired
    public JWTTokenValidatorFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(jwtUtils.getJwtHeader());
        if (null != jwt){
            try {
                if (jwtUtils.isTokenExpired(jwt)) throw new BadCredentialsException("Token expired!!");
                String authorities = jwtUtils.getAuthorities(jwt);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        jwtUtils.extractUsername(jwt),
                        null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

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
