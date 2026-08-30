package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity ghi nhận thông tin đăng ký khóa học của học viên.
 * Đảm bảo tính duy nhất: Mỗi cặp (student, course) chỉ có một bản ghi.
 */
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_enrollments_student_course",
                columnNames = {"student_id", "course_id"}
        ),
        indexes = {
                @Index(name = "idx_enrollments_course_id", columnList = "course_id"),
                @Index(name = "idx_enrollments_student_id", columnList = "student_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Học viên thực hiện đăng ký (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;

    /**
     * Khóa học được đăng ký (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    /**
     * Thời điểm đăng ký thành công.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime enrolledAt;

    /**
     * Danh sách tiến độ học tập các bài học thuộc khóa học này.
     */
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    // ================= HELPER METHODS =================

    /**
     * Thêm tiến độ bài học mới và đồng bộ mối quan hệ hai chiều.
     */
    public void addLessonProgress(LessonProgress progress) {
        this.lessonProgresses.add(progress);
        progress.setEnrollment(this);
    }

    /**
     * Xóa tiến độ bài học và gỡ bỏ tham chiếu hai chiều.
     */
    public void removeLessonProgress(LessonProgress progress) {
        this.lessonProgresses.remove(progress);
        progress.setEnrollment(null);
    }
}