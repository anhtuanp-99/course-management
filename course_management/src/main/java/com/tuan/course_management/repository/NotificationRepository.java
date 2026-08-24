package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy danh sách thông báo của một user (có phân trang).
     */
    Page<Notification> findByUser(Long userId, Pageable pageable);

    /**
     * Lấy danh sách thông báo chưa đọc của một user.
     */
    Page<Notification> findByUserAndIsReadFalse(Long userId, Pageable pageable);

    /**
     * Đếm số thông báo chưa đọc của một user.
     */
    long countByUserIdAndIsReadFalse(Long userId);
}
