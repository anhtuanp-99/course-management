package com.tuan.course_management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuan.course_management.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JwtAuthenticationEntryPoint – Xử lý lỗi 401 Unauthorized.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Truy cập trái phép vào: {}", request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Giữ vững API Contract của toàn hệ thống
        ApiResponse<Void> apiResponse = ApiResponse.error("Bạn cần đăng nhập để truy cập tài nguyên này");

        // Ghi trực tiếp ra OutputStream để tối ưu tốc độ
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}