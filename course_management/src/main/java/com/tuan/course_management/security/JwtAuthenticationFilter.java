package com.tuan.course_management.security;

import com.tuan.course_management.enums.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bộ lọc tự động trích xuất và xác thực chuỗi JWT từ Authorization Header trên mỗi Request.
 * Tự động tạo đối tượng UserPrincipal trên RAM (Stateless) mà không cần truy vấn Database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);

            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Lấy toàn bộ thông tin Claims từ Token mà KHÔNG CẦN TRUY VẤN DATABASE
                Claims claims = jwtProvider.getClaimsFromToken(token);
                Long userId = claims.get("userId", Long.class);
                String roleStr = claims.get("role", String.class);
                String email = claims.getSubject();

                // Ánh xạ chuỗi Role từ Claim sang Enum Role
                Role role = Role.valueOf(roleStr);

                // Khởi tạo UserPrincipal với Enum Role đã refactor
                UserPrincipal userDetails = UserPrincipal.builder()
                        .id(userId)
                        .email(email)
                        .role(role)
                        .active(true)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Lưu thông tin xác thực vào Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("Không thể xác thực người dùng từ JWT Token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Trích xuất chuỗi JWT Token từ Header Authorization (Bearer Token).
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}