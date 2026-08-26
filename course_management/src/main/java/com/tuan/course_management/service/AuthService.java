package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * AuthService – Xử lý logic xác thực.
 * Lý do tạo: Tách biệt logic xác thực khỏi Controller, ủy quyền cho AuthenticationManager.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    /**
     * Xác thực username/password.
     * - Nếu đúng: trả về Authentication object (chứa user + roles).
     * - Nếu sai: ném BadCredentialsException.
     */
    public Authentication authenticate(LoginRequest request) {
        log.debug("Xác thực user với email: {}", request.getEmail());

        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication auth = authenticationManager.authenticate(token);

            log.info("Xác thực thành công cho email: {}", request.getEmail());
            return auth;
        } catch (BadCredentialsException e) {
            log.warn("Xác thực thất bại cho email: {}", request.getEmail());
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }
    }
}
