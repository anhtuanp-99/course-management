package com.tuan.course_management.repository;

import com.tuan.course_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo email (dùng cho login)
    Optional<User> findByEmail(String email);

    // Kiểm tra email đã tồn tại chưa (dùng cho register)
    boolean existsByEmail(String email);

    /**
     * Lấy danh sách user có phân trang và lọc theo role.
     * @param role ADMIN, TEACHER, STUDENT (có thể null)
     * @param pageable thông tin phân trang
     * @return Page<User>
     */
    @Query("SELECT u FROM User u WHERE (:role IS NULL or u.role = :role)")
    Page<User> findByRole(@Param("role") String role, Pageable pageable);


    /**
     * Lấy danh sách user theo trạng thái active/inactive.
     */
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}
