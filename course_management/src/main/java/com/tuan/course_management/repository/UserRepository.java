package com.tuan.course_management.repository;

import com.tuan.course_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface truy xuất dữ liệu người dùng.
 * Tích hợp JpaSpecificationExecutor để hỗ trợ lọc động theo role, status và search (STT 4, 31).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}