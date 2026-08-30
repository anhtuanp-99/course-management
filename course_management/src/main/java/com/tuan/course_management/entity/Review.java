package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity quản lý đánh giá và nhận xét khóa học của học viên.
 * Đảm bảo tính duy nhất: Mỗi học viên chỉ được gửi duy nhất một đánh giá cho mỗi khóa học.
 */
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reviews_student_course",
                columnNames = {"student_id", "course_id"}
        ),
        indexes = {
                @Index(name = "idx_reviews_course_id", columnList = "course_id"),
                @Index(name = "idx_reviews_student_id", columnList = "student_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Số sao đánh giá (Giá trị hợp lệ từ 1 đến 5).
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Nội dung nhận xét/bình luận của học viên.
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * Học viên viết đánh giá (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;

    /**
     * Khóa học được đánh giá (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ================= DOMAIN BUSINESS METHODS =================

    /**
     * Cập nhật nội dung đánh giá và số sao.
     *
     * @param newRating  Số sao mới (từ 1 đến 5)
     * @param newComment Nội dung nhận xét mới
     */
    public void updateReview(int newRating, String newComment) {
        if (newRating >= 1 && newRating <= 5) {
            this.rating = newRating;
        }
        if (newComment != null) {
            this.comment = newComment.trim();
        }
    }
}