package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity thông báo.
 * Lưu thông báo gửi đến người dùng (có thể là thông báo chung hoặc cá nhân).
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * Người nhận thông báo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    /**
     * Trạng thái đã đọc: true = đã đọc, false = chưa đọc.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean read = false;

    /**
     * Thời điểm tạo thông báo.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}