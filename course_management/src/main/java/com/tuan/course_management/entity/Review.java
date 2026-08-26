package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity đánh giá khóa học.
 * Học viên sau khi đăng ký có thể gửi đánh giá (rating + comment) cho khóa học.
 * Mỗi học viên chỉ được gửi một đánh giá cho một khóa học (unique).
 */
@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Số sao đánh giá (1-5).
     * Validation sẽ được thực hiện ở tầng DTO.
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Nội dung bình luận.
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * Học viên viết đánh giá.
     * Quan hệ nhiều đánh giá - một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;

    /**
     * Khóa học được đánh giá.
     * Quan hệ nhiều đánh giá - một khóa học.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}