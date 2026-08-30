package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity quản lý thông tin thông báo trong hệ thống.
 * Hỗ trợ cả thông báo cá nhân (gán user_id) và thông báo chung (user_id = null).
 */
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_id", columnList = "user_id"),
                @Index(name = "idx_notifications_user_read", columnList = "user_id, is_read")
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
     * Tiêu đề thông báo.
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Nội dung thông báo.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Người nhận thông báo (null nếu là thông báo chung cho toàn hệ thống).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    /**
     * Trạng thái đã đọc: true = đã đọc, false = chưa đọc.
     */
    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    /**
     * Thời điểm đọc thông báo (null nếu chưa đọc).
     */
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ================= DOMAIN BUSINESS METHODS =================

    /**
     * Đánh dấu thông báo đã đọc và tự động lưu lại thời điểm.
     */
    public void markAsRead() {
        if (!this.read) {
            this.read = true;
            this.readAt = LocalDateTime.now();
        }
    }
}