package com.tuan.course_management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("Truy cập bị từ chối do không đủ quyền: {} - Reason: {}",
                request.getRequestURI(), accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Gọi đúng factory method nhận ErrorCode của ApiResponse
        ApiResponse<Void> apiResponse = ApiResponse.error(ErrorCode.FORBIDDEN_RESOURCE);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}