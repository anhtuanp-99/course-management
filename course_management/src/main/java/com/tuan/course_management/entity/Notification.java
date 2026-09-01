package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ các thông điệp, sự kiện hệ thống tạo tự động hoặc do Admin gửi tới người dùng.
 */
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_id", columnList = "user_id"),
                @Index(name = "idx_notifications_user_read", columnList = "user_id, read")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Người dùng nhận thông báo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * Nội dung chi tiết thông báo.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    /**
     * Phân loại sự kiện (VD: NEW_COURSE, LESSON_UPDATED, ENROLLMENT_CONFIRMED).
     */
    @Column(length = 50)
    private String type;

    /**
     * Đường dẫn điều hướng liên quan khi người dùng nhấp vào thông báo.
     */
    @Column(name = "target_url", length = 255)
    private String targetUrl;

    /**
     * Trạng thái đã đọc: true = đã đọc, false = chưa đọc.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean read = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ================= DOMAIN BUSINESS METHODS =================

    public void markAsRead() {
        this.read = true;
    }
}