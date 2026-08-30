package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity ghi nhận tiến độ hoàn thành bài học của học viên.
 * Đảm bảo tính duy nhất: Mỗi cặp (enrollment, lesson) chỉ tồn tại một bản ghi.
 */
@Entity
@Table(
        name = "lesson_progress",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_lesson_progress_enrollment_lesson",
                columnNames = {"enrollment_id", "lesson_id"}
        ),
        indexes = {
                @Index(name = "idx_lesson_progress_enrollment_id", columnList = "enrollment_id"),
                @Index(name = "idx_lesson_progress_lesson_id", columnList = "lesson_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Đợt đăng ký khóa học tương ứng (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    private Enrollment enrollment;

    /**
     * Bài học đang được theo dõi tiến độ (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    @ToString.Exclude
    private Lesson lesson;

    /**
     * Trạng thái hoàn thành: true = đã học xong, false = chưa hoàn thành.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean completed = false;

    /**
     * Thời điểm hoàn thành bài học (null nếu chưa hoàn thành).
     */
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ================= DOMAIN BUSINESS METHODS =================

    /**
     * Đánh dấu hoàn thành bài học và tự động lưu lại thời điểm.
     */
    public void markAsCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Hủy đánh dấu hoàn thành bài học.
     */
    public void markAsIncomplete() {
        this.completed = false;
        this.completedAt = null;
    }
}