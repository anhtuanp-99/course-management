package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.response.AuthResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
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
 * Dịch vụ xử lý các nghiệp vụ xác thực người dùng và quản lý phiên làm việc.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Xác thực thông tin tài khoản và cấp phát chuỗi Access Token.
     */
    public AuthResponse login(LoginRequest request) {
        log.debug("Bắt đầu xác thực đăng nhập cho username: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(userPrincipal);

        log.info("Đăng nhập thành công cho User ID: {}, Username: {}", userPrincipal.getId(), userPrincipal.getUsername());

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(userMapper.toSummaryResponse(user))
                .build();
    }

    /**
     * Kiểm tra tính hợp lệ của Token. Ném exception nếu token bị hỏng hoặc hết hạn.
     */
    public void verify(String token) {
        log.debug("Kiểm tra tính hợp lệ của JWT Token");

        if (token == null || token.isBlank() || !jwtProvider.validateToken(token)) {
            log.warn("Token không hợp lệ hoặc đã hết hạn");
            throw new AppException(ErrorCode.INVALID_JWT_TOKEN);
        }

        log.info("JWT Token hợp lệ");
    }

    /**
     * Truy vấn thông tin tài khoản cá nhân của người dùng đang đăng nhập.
     */
    public UserResponse getMe(UserPrincipal userPrincipal) {
        log.debug("Lấy thông tin tài khoản cá nhân cho User ID: {}", userPrincipal.getId());

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        log.info("Lấy thông tin tài khoản cá nhân thành công cho User ID: {}", user.getId());
        return userMapper.toResponse(user);
    }

    /**
     * Thực hiện đăng xuất người dùng khỏi hệ thống.
     */
    public void logout() {
        log.info("Thực hiện đăng xuất người dùng thành công");
    }
}