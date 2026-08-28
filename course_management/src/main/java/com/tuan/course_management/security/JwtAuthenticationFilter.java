package com.tuan.course_management.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * JwtAuthenticationFilter: Lọc mỗi request, lấy JWT từ header Authorization,
 * bóc tách Claims tạo UserPrincipal trên RAM và set vào SecurityContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);

            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Lấy toàn bộ thông tin Claims từ Token mà KHÔNG BẤM DATABASE
                Claims claims = jwtProvider.getClaimsFromToken(token);
                Long userId = claims.get("userId", Long.class);
                String role = claims.get("role", String.class);
                String email = claims.getSubject();

                // Tự dựng UserPrincipal trực tiếp trên RAM (Stateless)
                UserPrincipal userDetails = UserPrincipal.builder()
                        .id(userId)
                        .email(email)
                        .role(role)
                        .active(true)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("Không thể xác thực người dùng từ JWT Token: {}", e.getMessage());
        }

        // Tiếp tục chuỗi Filter
        filterChain.doFilter(request, response);
    }

    /**
     * Lấy JWT từ header Authorization (Bearer token).
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}