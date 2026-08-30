package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity quản lý thông tin bài học thuộc khóa học trong hệ thống.
 */
@Entity
@Table(
        name = "lessons",
        indexes = {
                @Index(name = "idx_lessons_course_id", columnList = "course_id"),
                @Index(name = "idx_lessons_course_published", columnList = "course_id, published")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Nội dung chi tiết bài học.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Trạng thái xuất bản: true = đã xuất bản, false = bản nháp.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean published = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Khóa học chứa bài học này (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    /**
     * Danh sách tiến độ học tập của học viên đối với bài học này.
     */
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    // ================= HELPER METHODS =================

    /**
     * Thêm tiến độ bài học mới và đồng bộ mối quan hệ hai chiều.
     */
    public void addLessonProgress(LessonProgress progress) {
        this.lessonProgresses.add(progress);
        progress.setLesson(this);
    }

    /**
     * Xóa tiến độ bài học và gỡ bỏ tham chiếu hai chiều.
     */
    public void removeLessonProgress(LessonProgress progress) {
        this.lessonProgresses.remove(progress);
        progress.setLesson(null);
    }
}