package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ phản hồi, xếp hạng sao và bình luận của sinh viên sau khi tham gia khóa học.
 */
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reviews_course_student",
                columnNames = {"course_id", "student_id"}
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
     * Khóa học nhận đánh giá.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    /**
     * Sinh viên viết đánh giá.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;

    /**
     * Điểm đánh giá xếp hạng sao (1 đến 5).
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Lời bình luận hoặc nhận xét chi tiết.
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ================= DOMAIN BUSINESS METHODS =================

    public void updateReview(int newRating, String newComment) {
        if (newRating >= 1 && newRating <= 5) {
            this.rating = newRating;
        }
        if (newComment != null) {
            this.comment = newComment.trim();
        }
    }
}