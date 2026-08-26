package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository thao tác với bảng notifications.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    /**
     * Lấy danh sách thông báo của một người dùng, có phân trang.
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    /**
     * Lấy danh sách thông báo chung (không gắn user cụ thể), có phân trang.
     */
    Page<Notification> findByUserIsNull(Pageable pageable);
}