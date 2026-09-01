package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository thao tác với dữ liệu thông báo hệ thống.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    /**
     * Lấy tất cả thông báo của một người dùng, có phân trang.
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    /**
     * Lấy danh sách thông báo chưa đọc của người dùng.
     */
    Page<Notification> findByUserIdAndReadFalse(Long userId, Pageable pageable);

    /**
     * Đếm số lượng thông báo chưa đọc của người dùng (Phục vụ hiển thị Badge UI).
     */
    long countByUserIdAndReadFalse(Long userId);
}