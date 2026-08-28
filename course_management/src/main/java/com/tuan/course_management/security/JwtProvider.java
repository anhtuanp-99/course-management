package com.tuan.course_management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtProvider: Tạo và xác thực JWT token (Tối ưu hiệu năng & Bảo mật cho JJWT 0.12.6).
 */
@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    public String generateAccessToken(UserPrincipal userPrincipal) {
        return generateToken(userPrincipal, jwtExpirationMs);
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return generateToken(userPrincipal, refreshExpirationMs);
    }

    private String generateToken(UserPrincipal userPrincipal, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // email
                .claim("userId", userPrincipal.getId())
                .claim("role", userPrincipal.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Lấy toàn bộ Claims từ Token (Bắt buộc phải có để Filter bóc tách userId, role mà không gọi DB).
     */
    public Claims getClaimsFromToken(String token) {
        return jwtParser.parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Lấy email (subject) từ Token.
     */
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Chữ ký JWT không hợp lệ hoặc bị giả mạo: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT đã hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT không được hỗ trợ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims rỗng: {}", e.getMessage());
        }
        return false;
    }
}