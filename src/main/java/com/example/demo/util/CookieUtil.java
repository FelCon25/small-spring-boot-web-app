package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

    @Value("${application.security.cookie.secure}")
    private boolean cookieSecure;

    public ResponseCookie createCookie(String name, String value, int maxAgeSecs, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .maxAge(maxAgeSecs)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie cleanCookie(String name, String path) {
        return createCookie(name, "", 0, path);
    }

    public String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null)
            return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
