package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.config.Constants;
import com.example.demo.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(CookieUtil cookieUtil, JwtService jwtService) {
        this.cookieUtil = cookieUtil;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            FilterChain filterChain) throws ServletException, IOException {
        String accessToken = cookieUtil.extractCookieValue(httpRequest, Constants.ACCESS_TOKEN_COOKIE_NAME);
        if (accessToken == null) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }
        if (!jwtService.isTokenValid(accessToken)) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }
        String userId = jwtService.extractSubject(accessToken);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId,
                null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(httpRequest, httpResponse);
    }
}
