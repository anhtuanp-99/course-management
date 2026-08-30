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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ quản lý thông tin người dùng và các nghiệp vụ tài khoản liên quan.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng ký tài khoản người dùng mới với vai trò mặc định.
     *
     * @param request Thông tin đăng ký từ phía Client
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
     * Lấy danh sách người dùng theo điều kiện lọc và phân trang.
     *
     * @param page Số trang truy vấn
     * @param size Số lượng bản ghi trên một trang
     * @param sortBy Cột thực hiện sắp xếp
     * @param sortDir Hướng sắp xếp (ASC/DESC)
     * @param role Lọc theo vai trò
     * @param isActive Lọc theo trạng thái tài khoản
     * @return PageResponse Danh sách người dùng dạng DTO phân trang
     */
    public PageResponse<UserResponse> getUsers(int page, int size, String sortBy, String sortDir,
                                               Role role, Boolean isActive) {
        log.debug("Truy vấn danh sách người dùng với bộ lọc - Page: {}, Size: {}, Role: {}, Active: {}", page, size, role, isActive);

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
     * Lấy thông tin chi tiết một người dùng theo mã định danh.
     *
     * @param userId Mã định danh người dùng
     * @return UserResponse Dữ liệu thông tin người dùng
     */
    public UserResponse getUserById(Long userId) {
        log.debug("Truy vấn thông tin người dùng cho User ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserMapper.toResponse(user);
    }

    /**
     * Khởi tạo tài khoản người dùng mới từ giao diện quản trị.
     *
     * @param request Thông tin tài khoản cần tạo
     * @return UserResponse Dữ liệu tài khoản sau khi tạo
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Thực hiện tạo tài khoản mới từ quản trị cho email: {}", request.getEmail());

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
        log.info("Tạo người dùng thành công cho User ID: {}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    /**
     * Cập nhật thông tin hồ sơ của người dùng.
     *
     * @param userId Mã định danh người dùng cần cập nhật
     * @param request Dữ liệu hồ sơ mới
     * @return UserResponse Thông tin người dùng sau khi cập nhật
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        log.debug("Thực hiện cập nhật thông tin hồ sơ cho User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);
        log.info("Cập nhật thông tin hồ sơ thành công cho User ID: {}", updatedUser.getId());

        return UserMapper.toResponse(updatedUser);
    }

    /**
     * Thay đổi vai trò người dùng trong hệ thống.
     *
     * @param userId Mã định danh người dùng bị thay đổi vai trò
     * @param request Vai trò mới
     * @param currentAdminId Mã định danh Admin đang thực hiện thao tác
     * @return UserResponse Thông tin người dùng sau khi đổi vai trò
     */
    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateRoleRequest request, Long currentAdminId) {
        log.debug("Thay đổi vai trò người dùng ID: {} bởi Admin ID: {}", userId, currentAdminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == Role.ADMIN && userId.equals(currentAdminId)) {
            throw new AppException(ErrorCode.CANNOT_CHANGE_OWN_ROLE);
        }

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);
        log.info("Cập nhật vai trò thành công cho User ID: {}, Vai trò mới: {}", updatedUser.getId(), updatedUser.getRole());

        return UserMapper.toResponse(updatedUser);
    }

    /**
     * Cập nhật trạng thái kích hoạt hoặc khóa tài khoản.
     *
     * @param userId Mã định danh người dùng
     * @param request Trạng thái mới
     * @return UserResponse Thông tin người dùng sau khi đổi trạng thái
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, UpdateStatusRequest request) {
        log.debug("Thay đổi trạng thái hoạt động cho User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setActive(request.getActive());
        User updatedUser = userRepository.save(user);
        log.info("Cập nhật trạng thái thành công cho User ID: {}, Active: {}", updatedUser.getId(), updatedUser.isActive());

        return UserMapper.toResponse(updatedUser);
    }

    /**
     * Xóa tài khoản người dùng khỏi cơ sở dữ liệu.
     *
     * @param userId Mã định danh người dùng cần xóa
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
     * Thay đổi mật khẩu người dùng.
     *
     * @param userId Mã định danh người dùng
     * @param request Dữ liệu mật khẩu cũ và mật khẩu mới
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
        userRepository.save(user);
        log.info("Đổi mật khẩu thành công cho User ID: {}", userId);
    }
}