package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity theo dõi chi tiết việc truy cập và hoàn thành từng bài học của sinh viên.
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
     * Lượt đăng ký tương ứng của sinh viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    private Enrollment enrollment;

    /**
     * Bài học đang được theo dõi tiến độ.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    @ToString.Exclude
    private Lesson lesson;

    /**
     * Đánh dấu sinh viên đã hoàn thành bài học này hay chưa.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean completed = false;

    /**
     * Thời điểm sinh viên đánh dấu hoàn thành bài học.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Thời điểm gần nhất sinh viên truy cập vào bài học (Tự động cập nhật).
     */
    @UpdateTimestamp
    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ================= DOMAIN BUSINESS METHODS =================

    /**
     * Đánh dấu hoàn thành bài học và tự động lưu thời điểm.
     */
    public void markAsCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Hủy đánh dấu hoàn thành bài học.
     */
    public void markAsIncomplete() {
        this.completed = false;
        this.completedAt = null;
        this.lastAccessedAt = LocalDateTime.now();
    }

    /**
     * Cập nhật thời điểm truy cập bài học gần nhất.
     */
    public void touch() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}