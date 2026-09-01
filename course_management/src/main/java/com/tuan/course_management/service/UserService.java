package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.ChangePasswordRequest;
import com.tuan.course_management.dto.request.RegisterRequest;
import com.tuan.course_management.dto.request.UserCreateRequest;
import com.tuan.course_management.dto.request.UserUpdateRequest;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.UserMapper;
import com.tuan.course_management.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "username", "fullName", "email", "role", "active", "createdAt"
    );

    /**
     * Đăng ký tài khoản người dùng mới (Mặc định vai trò STUDENT).
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.debug("Bắt đầu đăng ký tài khoản mới cho username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Đăng ký tài khoản thành công cho User ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    /**
     * Lấy danh sách người dùng có phân trang, tìm kiếm và lọc động (Đáp ứng STT 4, 31).
     */
    public PageResponse<UserResponse> getUsers(int page, int size, String sortBy, String sortDir,
                                               String search, Role role, Boolean isActive) {
        log.debug("Truy vấn danh sách người dùng - Page: {}, Size: {}, Search: {}, Role: {}, Active: {}",
                page, size, search, role, isActive);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(safeSortBy).ascending() : Sort.by(safeSortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm theo từ khóa (username, fullName hoặc email)
            if (StringUtils.hasText(search)) {
                String keyword = "%" + search.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), keyword),
                        cb.like(cb.lower(root.get("fullName")), keyword),
                        cb.like(cb.lower(root.get("email")), keyword)
                ));
            }

            // 2. Lọc theo vai trò (ROLE)
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            // 3. Lọc theo trạng thái hoạt động (ACTIVE)
            if (isActive != null) {
                predicates.add(cb.equal(root.get("active"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<UserResponse> mappedContent = userPage.getContent().stream()
                .map(userMapper::toResponse)
                .toList();

        return PageResponse.from(userPage, mappedContent);
    }

    /**
     * Lấy thông tin chi tiết một người dùng theo ID (Đáp ứng STT 5).
     */
    public UserResponse getUserById(Long userId) {
        log.debug("Truy vấn thông tin người dùng cho User ID: {}", userId);
        User user = getUserEntityById(userId);
        return userMapper.toResponse(user);
    }

    /**
     * Khởi tạo tài khoản người dùng mới từ màn hình ADMIN (Đáp ứng STT 6).
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.debug("ADMIN tạo tài khoản mới cho username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("ADMIN tạo người dùng thành công cho User ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng (Đáp ứng STT 26). Tận dụng JPA Dirty Checking.
     */
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        log.debug("Thực hiện cập nhật thông tin hồ sơ cho User ID: {}", userId);

        User user = getUserEntityById(userId);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        log.info("Cập nhật thông tin hồ sơ thành công cho User ID: {}", user.getId());
        return userMapper.toResponse(user);
    }

    /**
     * Cập nhật vai trò người dùng (Đáp ứng STT 7).
     * Quy tắc nghiệp vụ: ADMIN không được phép cập nhật vai trò của chính mình và của ADMIN khác.
     */
    @Transactional
    public UserResponse updateUserRole(Long userId, Role newRole, Long currentAdminId) {
        log.debug("Thao tác thay đổi vai trò User ID: {} bởi Admin ID: {}", userId, currentAdminId);

        User targetUser = getUserEntityById(userId);

        if (targetUser.getRole() == Role.ADMIN) {
            if (userId.equals(currentAdminId)) {
                throw new AppException(ErrorCode.CANNOT_CHANGE_OWN_ROLE);
            } else {
                throw new AppException(ErrorCode.CANNOT_UPDATE_ADMIN_ROLE);
            }
        }

        targetUser.setRole(newRole);
        log.info("Cập nhật vai trò thành công cho User ID: {}, Role mới: {}", targetUser.getId(), targetUser.getRole());

        return userMapper.toResponse(targetUser);
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa tài khoản người dùng (Đáp ứng STT 8).
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, boolean active) {
        log.debug("Thay đổi trạng thái hoạt động cho User ID: {}", userId);

        User user = getUserEntityById(userId);

        user.setActive(active);
        log.info("Cập nhật trạng thái thành công cho User ID: {}, Active: {}", user.getId(), user.isActive());

        return userMapper.toResponse(user);
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

        User user = getUserEntityById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        log.info("Đổi mật khẩu thành công cho User ID: {}", userId);
    }

    // =========================================================================
    // HELPER METHODS (Dùng nội bộ và chia sẻ liên Service)
    // =========================================================================

    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User getTeacherEntityById(Long teacherId) {
        User teacher = getUserEntityById(teacherId);
        if (teacher.getRole() != Role.TEACHER) {
            log.warn("User ID: {} có vai trò {} nhưng không phải TEACHER", teacherId, teacher.getRole());
            throw new AppException(ErrorCode.USER_NOT_TEACHER);
        }
        return teacher;
    }
}