package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.*;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.BadRequestException;
import com.tuan.course_management.exception.ConflictException;
import com.tuan.course_management.exception.ForbiddenException;
import com.tuan.course_management.exception.ResourceNotFoundException;
import com.tuan.course_management.mapper.UserMapper;
import com.tuan.course_management.repository.UserRepository;
import com.tuan.course_management.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService: Quản lý các nghiệp vụ liên quan đến người dùng.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng ký tài khoản người dùng mới.
     */
    @Transactional
    public void register(RegisterRequest request) {
        log.debug("Đăng ký tài khoản mới: email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.STUDENT)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Đăng ký tài khoản thành công. UserID: {}", savedUser.getId());
    }

    /**
     * Lấy danh sách người dùng có phân trang và bộ lọc.
     */
    public PageResponse<UserResponse> getUsers(int page, int size, String sortBy, String sortDir,
                                               Role role, Boolean isActive) {
        log.debug("Lấy danh sách người dùng: page={}, size={}, role={}, isActive={}", page, size, role, isActive);

        Pageable pageable = PageUtils.createPageable(page, size, sortBy, sortDir, "createdAt");

        Specification<User> spec = Specification.where(null);

        if (role != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), role));
        }
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), isActive));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<UserResponse> responsePage = userPage.map(UserMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    /**
     * Lấy thông tin chi tiết người dùng theo ID.
     */
    public UserResponse getUserById(Long userId) {
        log.debug("Lấy thông tin người dùng ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        return UserMapper.toResponse(user);
    }

    /**
     * Tạo mới người dùng từ trang quản trị.
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Bắt đầu tạo người dùng mới: email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng: " + request.getEmail());
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
        log.info("Tạo người dùng thành công. UserID: {}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        log.debug("Cập nhật hồ sơ người dùng ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email đã được sử dụng: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);
        log.info("Cập nhật hồ sơ thành công. UserID: {}", updatedUser.getId());

        return UserMapper.toResponse(updatedUser);
    }

    /**
     * Thay đổi vai trò người dùng hệ thống.
     */
    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateRoleRequest request, Long currentAdminId) {
        log.debug("Cập nhật role người dùng ID: {} bởi Admin ID: {}", userId, currentAdminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (user.getRole() == Role.ADMIN && userId.equals(currentAdminId)) {
            throw new ForbiddenException("Không thể tự thay đổi role của chính mình");
        }

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);
        log.info("Cập nhật role thành công. UserID: {}, Role mới: {}", updatedUser.getId(), updatedUser.getRole());

        return UserMapper.toResponse(updatedUser);
    }

    /**
     * Khóa hoặc mở khóa tài khoản người dùng.
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, UpdateStatusRequest request) {
        log.debug("Cập nhật trạng thái người dùng ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        user.setActive(request.getActive());
        User updatedUser = userRepository.save(user);
        log.info("Cập nhật trạng thái thành công. UserID: {}, active: {}", updatedUser.getId(), updatedUser.isActive());

        return UserMapper.toResponse(updatedUser);
    }

    /**
     * Xóa tài khoản người dùng khỏi hệ thống.
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Xóa người dùng ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("Xóa người dùng thành công. UserID: {}", userId);
    }

    /**
     * Thay đổi mật khẩu tài khoản.
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.debug("Đổi mật khẩu cho người dùng ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Đổi mật khẩu thành công. UserID: {}", userId);
    }
}