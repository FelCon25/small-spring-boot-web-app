package com.example.demo.service;

import com.example.demo.config.Constants;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Session;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.InvalidTokenException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.util.CookieUtil;
import com.example.demo.util.TimeUtil;
import com.example.demo.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for authentication operations including user
 * registration,
 * login, and token refresh. Coordinates validation, token generation, session
 * management, and secure cookie delivery.
 */
@Service
public class AuthService {

    @Value("${application.security.jwt.expiration.refresh-token}")
    private long refreshTokenExpirationTime;

    @Value("${application.security.jwt.expiration.access-token}")
    private long accessTokenExpirationTime;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final CookieUtil cookieUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserMapper userMapper, SessionRepository sessionRepository,
            RefreshTokenRepository refreshTokenRepository, JwtService jwtService, CookieUtil cookieUtils,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.cookieUtils = cookieUtils;
        this.passwordEncoder = passwordEncoder;
    }

    // ──────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────

    /**
     * Registers a new user, creates a session, generates tokens,
     * and attaches them as HttpOnly cookies to the response.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request, String ipAddress, String userAgent,
            HttpServletResponse httpResponse) {

        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExistsException("User", "username", request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("User", "email", request.email());
        }

        User user = userMapper.toEntity(request);
        user.setHashPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        return authenticateAndRespond(savedUser, ipAddress, userAgent, httpResponse);
    }

    /**
     * Authenticates a user by email and password, creates a new session,
     * generates tokens, and attaches them as HttpOnly cookies.
     */
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent,
            HttpServletResponse httpResponse) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getHashPassword())) {
            throw new InvalidCredentialsException();
        }

        return authenticateAndRespond(user, ipAddress, userAgent, httpResponse);
    }

    /**
     * Validates the refresh token from the request cookie, performs token rotation
     * (revokes old token, issues new one), and returns fresh cookies.
     * Implements replay detection: if a revoked token is reused, the entire
     * session is revoked as a security precaution against potential token theft.
     */
    @Transactional
    public AuthResponse refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        // Extract the raw refresh token from the cookie
        String rawToken = cookieUtils.extractCookieValue(httpRequest, Constants.REFRESH_TOKEN_COOKIE_NAME);
        if (rawToken == null) {
            throw new InvalidTokenException("Refresh token not found");
        }

        // Look up the hashed token in the database
        String hashedToken = TokenUtil.hash(rawToken);
        RefreshToken oldRefreshToken = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not recognized"));

        // Retrieve the session early — needed for both validation and replay detection
        Session session = oldRefreshToken.getSession();

        // Replay detection: if an already-revoked token is reused, assume token theft
        // and revoke the entire session to protect the user
        if (oldRefreshToken.getRevokedAt() != null) {
            session.setRevokedAt(Instant.now());
            sessionRepository.save(session);
            throw new InvalidTokenException("Refresh token already used — session revoked for security");
        }

        // Validate: not expired
        if (oldRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(oldRefreshToken);
            throw new InvalidTokenException("Refresh token expired");
        }

        // Validate: session not revoked (e.g. by "logout from all devices")
        if (session.getRevokedAt() != null) {
            throw new InvalidTokenException("Session has been revoked");
        }

        User user = session.getUser();

        // --- Token Rotation ---
        // Revoke the old refresh token
        oldRefreshToken.setRevokedAt(Instant.now());

        // Generate a new refresh token and link it to the old one
        String newRawRefreshToken = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setTokenHash(TokenUtil.hash(newRawRefreshToken));
        newRefreshToken.setSession(session);
        newRefreshToken.setExpiresAt(TimeUtil.getExpirationFromNow(refreshTokenExpirationTime));

        oldRefreshToken.setReplacedBy(newRefreshToken);

        refreshTokenRepository.save(newRefreshToken);
        refreshTokenRepository.save(oldRefreshToken);

        // Update session activity timestamp
        session.setLastUsedAt(Instant.now());
        sessionRepository.save(session);

        // Generate a fresh access token
        Map<String, Object> extraClaims = new HashMap<>();
        String newAccessToken = jwtService.generateAccessToken(extraClaims, user);

        // Attach both cookies to the response
        attachTokenCookies(httpResponse, newAccessToken, newRawRefreshToken);

        return userMapper.toResponse(user);
    }

    /**
     * Logs out the current session by revoking the refresh token and its
     * associated session, then clears both authentication cookies.
     */
    @Transactional
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        // Extract the refresh token from the cookie to identify the session
        String rawToken = cookieUtils.extractCookieValue(httpRequest, Constants.REFRESH_TOKEN_COOKIE_NAME);

        if (rawToken != null) {
            String hashedToken = TokenUtil.hash(rawToken);
            refreshTokenRepository.findByTokenHash(hashedToken).ifPresent(refreshToken -> {
                // Revoke the refresh token
                refreshToken.setRevokedAt(Instant.now());
                refreshTokenRepository.save(refreshToken);

                // Revoke the associated session
                Session session = refreshToken.getSession();
                session.setRevokedAt(Instant.now());
                sessionRepository.save(session);
            });
        }

        // Always clear cookies, even if the token was already invalid
        clearTokenCookies(httpResponse);
    }

    // ──────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────

    /**
     * Creates a new session + tokens for the given user and attaches cookies.
     * Shared by both register and login flows.
     */
    private AuthResponse authenticateAndRespond(User user, String ipAddress, String userAgent,
            HttpServletResponse httpResponse) {

        // Create session
        Session session = new Session();
        session.setUser(user);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setLastUsedAt(Instant.now());
        sessionRepository.save(session);

        // Generate access token (JWT)
        Map<String, Object> extraClaims = new HashMap<>();
        String accessToken = jwtService.generateAccessToken(extraClaims, user);

        // Generate and persist refresh token (opaque, hashed)
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(TokenUtil.hash(rawRefreshToken));
        refreshToken.setSession(session);
        refreshToken.setExpiresAt(TimeUtil.getExpirationFromNow(refreshTokenExpirationTime));
        refreshTokenRepository.save(refreshToken);

        // Attach cookies to the HTTP response
        attachTokenCookies(httpResponse, accessToken, rawRefreshToken);

        return userMapper.toResponse(user);
    }

    /**
     * Builds and attaches access and refresh token cookies to the HTTP response.
     */
    private void attachTokenCookies(HttpServletResponse httpResponse, String accessToken, String rawRefreshToken) {

        ResponseCookie accessCookie = cookieUtils.createCookie(
                Constants.ACCESS_TOKEN_COOKIE_NAME, accessToken,
                TimeUtil.millisToSeconds(accessTokenExpirationTime),
                Constants.ACCESS_TOKEN_PATH);

        ResponseCookie refreshCookie = cookieUtils.createCookie(
                Constants.REFRESH_TOKEN_COOKIE_NAME, rawRefreshToken,
                TimeUtil.millisToSeconds(refreshTokenExpirationTime),
                Constants.REFRESH_TOKEN_PATH);

        httpResponse.addHeader("Set-Cookie", accessCookie.toString());
        httpResponse.addHeader("Set-Cookie", refreshCookie.toString());
    }

    /**
     * Clears both access and refresh token cookies from the browser.
     */
    private void clearTokenCookies(HttpServletResponse httpResponse) {
        httpResponse.addHeader("Set-Cookie",
                cookieUtils.cleanCookie(Constants.ACCESS_TOKEN_COOKIE_NAME, Constants.ACCESS_TOKEN_PATH).toString());
        httpResponse.addHeader("Set-Cookie",
                cookieUtils.cleanCookie(Constants.REFRESH_TOKEN_COOKIE_NAME, Constants.REFRESH_TOKEN_PATH).toString());
    }
}