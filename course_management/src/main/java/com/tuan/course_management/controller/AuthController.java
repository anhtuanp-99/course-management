package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.request.RegisterRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.JwtResponse;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.security.JwtProvider;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.AuthService;
import com.tuan.course_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    /**
     * POST /api/auth/register – Đăng ký tài khoản mới (public).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Nhận yêu cầu đăng kí tài khoản mới với email: {}", request.getEmail());

        userService.register(request);

        log.info("Đăng kí thành công cho email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng kí thành công! Vui lòng đăng nhập"));
    }

    /**
     * POST /api/auth/login – Đăng nhập (public).
     * Trả về Access Token và Refresh Token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Nhận yêu cầu đăng nhập với email: {}", request.getEmail());

        // 1. Xác thực
        Authentication authentication = authService.authenticate(request);

        // 2. Lấy thông tin user
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String email = principal.getUsername();
        Role role = principal.getUser().getRole();
        Long userId = principal.getUser().getId();
        String fullName = principal.getUser().getFullName();

        // 3. Tạo token
        String  accessToken = jwtProvider.generateToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        log.info("Đăng nhập thành công cho email: {}", email);

        // 4. Trả về response
        JwtResponse response = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .email(email)
                .fullName(fullName)
                .role(role.name())
                .build();
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));

    }
}
