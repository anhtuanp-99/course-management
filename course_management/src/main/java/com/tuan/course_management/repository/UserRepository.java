package com.tuan.course_management.repository;

import com.tuan.course_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface truy xuất dữ liệu liên quan đến thực thể người dùng trong cơ sở dữ liệu.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Tìm kiếm thông tin người dùng theo địa chỉ Email.
     *
     * @param email Địa chỉ Email cần tìm
     * @return Optional chứa thông tin người dùng nếu tồn tại
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra sự tồn tại của người dùng dựa trên địa chỉ Email.
     *
     * @param email Địa chỉ Email cần kiểm tra
     * @return boolean true nếu Email đã tồn tại, ngược lại false
     */
    boolean existsByEmail(String email);
}