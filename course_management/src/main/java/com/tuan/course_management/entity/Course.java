package com.tuan.course_management.entity;

import com.tuan.course_management.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity quản lý thông tin khóa học trong hệ thống.
 */
@Entity
@Table(
        name = "courses",
        indexes = {
                @Index(name = "idx_courses_teacher_id", columnList = "teacher_id"),
                @Index(name = "idx_courses_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Giảng viên phụ trách khóa học (Bắt buộc phải có).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ToString.Exclude
    private User teacher;

    /**
     * Trạng thái khóa học: DRAFT, PUBLISHED, ARCHIVED.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Danh sách bài học thuộc khóa học.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Lesson> lessons = new ArrayList<>();

    /**
     * Danh sách đăng ký của học viên vào khóa học.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Danh sách đánh giá khóa học.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Review> reviews = new ArrayList<>();

    // ================= HELPER METHODS =================

    /**
     * Thêm bài học mới và đồng bộ mối quan hệ hai chiều.
     */
    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
        lesson.setCourse(this);
    }

    /**
     * Xóa bài học khỏi khóa học và đồng bộ mối quan hệ hai chiều.
     */
    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
        lesson.setCourse(null);
    }
}