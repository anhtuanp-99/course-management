package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.request.RegisterRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.JwtResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.AuthService;
import com.tuan.course_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint liên quan đến xác thực và tài khoản.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Đăng ký tài khoản người dùng mới.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Nhận yêu cầu đăng ký tài khoản cho email: {}", request.getEmail());
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công!"));
    }

    /**
     * Đăng nhập hệ thống và nhận chuỗi Authentication Token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Nhận yêu cầu đăng nhập cho email: {}", request.getEmail());
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    /**
     * Lấy thông tin tài khoản của người dùng đang đăng nhập.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = authService.getMe(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kiểm tra tính hợp lệ của Token.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.verify(token));
    }

    /**
     * Đăng xuất khỏi hệ thống.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}