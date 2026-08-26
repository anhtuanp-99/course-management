package com.tuan.course_management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtAuthenticationFilter – Bộ lọc xác thực JWT cho mỗi request.
 * Lý do tạo: Spring Security mặc định không biết đọc JWT từ Header.
 * Kế thừa OncePerRequestFilter để đảm bảo chỉ thực thi một lần mỗi request.
 *
 * Quy trình:
 * 1. Lấy token từ Header "Authorization"
 * 2. Nếu có token → validate (ném exception nếu lỗi)
 * 3. Lấy email từ token → load UserDetails → tạo Authentication → set vào SecurityContext
 * 4. Nếu token lỗi → trả JSON 401 chi tiết
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                log.debug("Nhận token từ Header: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
            }

            jwtProvider.validateToken(token);
            String email = jwtProvider.getEmailFromToken(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("Xác thực thành công cho email: {}", email);
        } catch (ExpiredJwtException e) {
            log.warn("Token hết hạn: {}", e.getMessage());
            handleJwtException(response, "Token đã hết hạn. Vui lòng đăng nhập lại.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            log.warn("Token sai định dạng: {}", e.getMessage());
            handleJwtException(response, "Token sai định dạng.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (SignatureException e) {
            log.warn("Chữ ký token không hợp lệ: {}", e.getMessage());
            handleJwtException(response, "Chữ ký token không hợp lệ.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Lỗi xác thực JWT không xác định: {}", e.getMessage());
            handleJwtException(response, "Lỗi xác thực: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }

    }

    /**
     * Ghi JSON lỗi ra response.
     * KHÔNG gọi filterChain.doFilter() để request dừng lại.
     */
    private void handleJwtException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("timestamp", System.currentTimeMillis());

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    /**
     * Trích xuất token từ Header "Authorization".
     * Header có dạng: "Bearer <token>"
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
