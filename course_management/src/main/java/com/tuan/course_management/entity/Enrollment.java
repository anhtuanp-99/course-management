package com.tuan.course_management.entity;

import com.tuan.course_management.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity ghi nhận thông tin đăng ký khóa học của học viên và theo dõi tiến độ tổng quan.
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
                @Index(name = "idx_enrollments_student_id", columnList = "student_id"),
                @Index(name = "idx_enrollments_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Học viên thực hiện đăng ký (Role = STUDENT).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private User student;

    /**
     * Khóa học được sinh viên đăng ký.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    /**
     * Trạng thái tham gia khóa học (ENROLLED, COMPLETED, DROPPED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    /**
     * Tỷ lệ phần trăm tiến độ hoàn thành khóa học (0.00 - 100.00).
     */
    @Builder.Default
    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage = 0.00;

    /**
     * Ngày giờ sinh viên đăng ký thành công.
     */
    @CreationTimestamp
    @Column(name = "enrollment_date", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    /**
     * Ngày giờ sinh viên hoàn thành tất cả các bài học trong khóa.
     */
    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @OneToMany(mappedBy = "enrollment", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    // ================= HELPER METHODS =================

    public void addLessonProgress(LessonProgress progress) {
        this.lessonProgresses.add(progress);
        progress.setEnrollment(this);
    }

    public void removeLessonProgress(LessonProgress progress) {
        this.lessonProgresses.remove(progress);
        progress.setEnrollment(null);
    }
}