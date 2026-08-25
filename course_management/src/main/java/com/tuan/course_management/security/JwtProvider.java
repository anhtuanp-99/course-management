package com.tuan.course_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private int jwtRefreshExpirationMs;

    /**
     * Tạo SecretKey từ chuỗi secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo Access Token – thời gian sống ngắn (24h).
     */
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        log.debug("Access Token được tạo cho email: {}", email);
        return token;
    }

    /**
     * Tạo Refresh Token – thời gian sống dài hơn (7 ngày).
     */
    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);

        String token = Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        log.debug("Refresh Token được tạo cho email: {}", email);
        return token;
    }

    /**
     * Trích xuất email từ token.
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Trích xuất roles từ token.
     */
    public String getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("roles", String.class);
    }

    /**
     * Xác thực token – ném exception nếu token lỗi.
     * - ExpiredJwtException: hết hạn
     * - MalformedJwtException: sai định dạng
     * - SignatureException: chữ ký không khớp
     */
    public void validateToken(String token) throws ExpiredJwtException, MalformedJwtException, SignatureException {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
        log.debug("Token được xác thực thành công");
    }

}
