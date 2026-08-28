package com.tuan.course_management.repository;

import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository thao tác với bảng users.
 * - JpaRepository: cung cấp CRUD và phân trang cơ bản.
 * - JpaSpecificationExecutor: hỗ trợ truy vấn động (Specification) kết hợp Pageable.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Tìm người dùng theo email (dùng cho đăng nhập).
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra email đã tồn tại hay chưa.
     */
    boolean existsByEmail(String email);

    /**
     * Lấy danh sách người dùng theo vai trò, có phân trang.
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Lấy danh sách người dùng theo trạng thái hoạt động, có phân trang.
     */
    Page<User> findByActive(boolean active, Pageable pageable);

    /**
     * Lấy danh sách người dùng theo vai trò và trạng thái, có phân trang.
     */
    Page<User> findByRoleAndActive(Role role, boolean isActive, Pageable pageable);
}