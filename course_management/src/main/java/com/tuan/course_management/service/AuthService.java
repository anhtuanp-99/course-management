package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.JwtResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.mapper.UserMapper;
import com.tuan.course_management.repository.UserRepository;
import com.tuan.course_management.security.JwtProvider;
import com.tuan.course_management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService: Xử lý nghiệp vụ đăng nhập, xác thực token và lấy thông tin người dùng.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * Xác thực tài khoản và cấp phát chuỗi JWT Token.
     */
    public JwtResponse login(LoginRequest request) {
        log.debug("Bắt đầu xác thực đăng nhập cho email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtProvider.generateRefreshToken(userPrincipal);

        log.info("Đăng nhập thành công. UserID: {}, Email: {}", userPrincipal.getId(), userPrincipal.getUsername());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .email(userPrincipal.getUsername())
                .fullName(userPrincipal.getFullName())
                .role(userPrincipal.getRole())
                .build();
    }

    /**
     * Kiểm tra trạng thái hiệu lực của Token.
     */
    public ApiResponse<Void> verify(String token) {
        log.debug("Xác thực trạng thái token");

        if (token == null || token.isBlank()) {
            log.warn("Token không được cung cấp");
            return ApiResponse.error("Token không hợp lệ");
        }

        boolean isValid = jwtProvider.validateToken(token);
        if (isValid) {
            log.info("Token hợp lệ");
            return ApiResponse.success("Token hợp lệ", null);
        } else {
            log.warn("Token không hợp lệ hoặc đã hết hạn");
            return ApiResponse.error("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    /**
     * Lấy thông tin chi tiết người dùng hiện tại từ Database.
     */
    @Transactional(readOnly = true)
    public UserResponse getMe(UserPrincipal userPrincipal) {
        log.debug("Lấy thông tin người dùng từ UserPrincipal ID: {}", userPrincipal.getId());

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));

        log.info("Lấy thông tin người dùng thành công. UserID: {}", user.getId());
        return UserMapper.toResponse(user);
    }

    /**
     * Đăng xuất khỏi hệ thống.
     */
    public ApiResponse<Void> logout() {
        log.info("Đăng xuất thành công");
        return ApiResponse.success("Đăng xuất thành công", null);
    }
}