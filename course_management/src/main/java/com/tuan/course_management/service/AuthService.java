package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.request.RefreshTokenRequest;
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
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthResponse login(LoginRequest request) {
        log.debug("Bắt đầu xác thực đăng nhập cho username: {}", request.getUsername());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword())
            );
        } catch (DisabledException | LockedException ex) {
            log.warn("Đăng nhập thất bại do tài khoản bị vô hiệu hóa hoặc bị khóa: {}", request.getUsername());
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        } catch (BadCredentialsException ex) {
            log.warn("Đăng nhập thất bại do sai tài khoản hoặc mật khẩu: {}", request.getUsername());
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        } catch (AuthenticationException ex) {
            log.warn("Lỗi xác thực không xác định cho username {}: {}", request.getUsername(), ex.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtProvider.generateRefreshToken(userPrincipal);

        log.info("Đăng nhập thành công cho User ID: {}", userPrincipal.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getExpirationInSeconds())
                .user(userMapper.toSummaryResponse(user))
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank() || !jwtProvider.validateToken(refreshToken)) {
            log.warn("Refresh Token không hợp lệ hoặc đã hết hạn");
            throw new AppException(ErrorCode.INVALID_JWT_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        String newAccessToken = jwtProvider.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtProvider.generateRefreshToken(userPrincipal);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getExpirationInSeconds())
                .user(userMapper.toSummaryResponse(user))
                .build();
    }

    public void verify(String token) {
        if (token == null || token.isBlank() || !jwtProvider.validateToken(token)) {
            throw new AppException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    public UserResponse getMe(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    public void logout() {
        log.info("Đăng xuất người dùng thành công");
    }
}