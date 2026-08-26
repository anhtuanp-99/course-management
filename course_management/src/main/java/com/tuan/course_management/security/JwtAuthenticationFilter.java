package com.tuan.course_management.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
public class JwtAuthenticationFilter {




}
