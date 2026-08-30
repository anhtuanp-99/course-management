package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.*;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.UserMapper;
import com.tuan.course_management.repository.UserRepository;
import com.tuan.course_management.util.PageUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý các nghiệp vụ quản lý thông tin người dùng và tài khoản hệ thống.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "fullName", "email", "role", "active", "createdAt"
    );

    /**
     * Đăng ký tài khoản người dùng mới (Mặc định vai trò STUDENT).
     */
    @Transactional
    public void register(RegisterRequest request) {
        log.debug("Bắt đầu đăng ký tài khoản mới cho email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.STUDENT)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Đăng ký tài khoản thành công cho User ID: {}", savedUser.getId());
    }

    /**
     * Lấy danh sách người dùng theo điều kiện lọc và phân trang (Đáp ứng STT 4, 31).
     */
    public PageResponse<UserResponse> getUsers(int page, int size, String sortBy, String sortDir,
                                               Role role, Boolean isActive) {
        log.debug("Truy vấn danh sách người dùng - Page: {}, Size: {}, Role: {}, Active: {}", page, size, role, isActive);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "createdAt");

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (isActive != null) {
                predicates.add(cb.equal(root.get("active"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);
        return PageResponse.from(userPage.map(UserMapper::toResponse));
    }

    /**
     * Lấy thông tin chi tiết một người dùng theo ID (Đáp ứng STT 5).
     */
    public UserResponse getUserById(Long userId) {
        log.debug("Truy vấn thông tin người dùng cho User ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserMapper.toResponse(user);
    }

    /**
     * Khởi tạo tài khoản người dùng mới từ màn hình ADMIN (Đáp ứng STT 6).
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("ADMIN tạo tài khoản mới cho email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phone(request.getPhone())
                .active(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("ADMIN tạo người dùng thành công cho User ID: {}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng (Đáp ứng STT 26). Tận dụng JPA Dirty Checking.
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        log.debug("Thực hiện cập nhật thông tin hồ sơ cho User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        log.info("Cập nhật thông tin hồ sơ thành công cho User ID: {}", user.getId());
        return UserMapper.toResponse(user);
    }

    /**
     * Cập nhật vai trò người dùng (Đáp ứng STT 7).
     * Quy tắc nghiệp vụ: ADMIN không được phép cập nhật vai trò của chính mình và của ADMIN khác.
     */
    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateRoleRequest request, Long currentAdminId) {
        log.debug("Thao tác thay đổi vai trò User ID: {} bởi Admin ID: {}", userId, currentAdminId);

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra quy tắc bảo vệ vai trò Quản trị viên
        if (targetUser.getRole() == Role.ADMIN) {
            if (userId.equals(currentAdminId)) {
                throw new AppException(ErrorCode.CANNOT_CHANGE_OWN_ROLE);
            } else {
                throw new AppException(ErrorCode.CANNOT_UPDATE_ADMIN_ROLE);
            }
        }

        targetUser.setRole(request.getRole());
        log.info("Cập nhật vai trò thành công cho User ID: {}, Role mới: {}", targetUser.getId(), targetUser.getRole());

        return UserMapper.toResponse(targetUser);
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa tài khoản người dùng (Đáp ứng STT 8).
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, UpdateStatusRequest request) {
        log.debug("Thay đổi trạng thái hoạt động cho User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setActive(request.getActive());
        log.info("Cập nhật trạng thái thành công cho User ID: {}, Active: {}", user.getId(), user.isActive());

        return UserMapper.toResponse(user);
    }

    /**
     * Xóa tài khoản người dùng khỏi hệ thống (Đáp ứng STT 9).
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Thực hiện xóa người dùng cho User ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
        log.info("Xóa người dùng thành công cho User ID: {}", userId);
    }

    /**
     * Thay đổi mật khẩu tài khoản người dùng (Đáp ứng STT 27).
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.debug("Thực hiện đổi mật khẩu cho User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        log.info("Đổi mật khẩu thành công cho User ID: {}", userId);
    }
}