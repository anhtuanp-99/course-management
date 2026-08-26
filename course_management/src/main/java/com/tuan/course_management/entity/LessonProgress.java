package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity tiến độ bài học.
 * Mỗi bản ghi đánh dấu một học viên đã hoàn thành một bài học cụ thể
 * trong một khóa học đã đăng ký hay chưa.
 */
@Entity
@Table(name = "lesson_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Enrollment tương ứng (học viên đã đăng ký khóa học nào).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    private Enrollment enrollment;

    /**
     * Bài học đang được theo dõi tiến độ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @ToString.Exclude
    private Lesson lesson;

    /**
     * Trạng thái hoàn thành: true = đã học xong, false = chưa hoàn thành.
     */
    @Column(nullable = false)
    private boolean completed;

    /**
     * Thời điểm hoàn thành bài học (nullable nếu chưa hoàn thành).
     */
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}