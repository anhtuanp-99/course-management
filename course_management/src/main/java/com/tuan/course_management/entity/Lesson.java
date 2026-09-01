package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity quản lý danh mục bài học và nội dung giảng dạy trong khóa học.
 */
@Entity
@Table(
        name = "lessons",
        indexes = {
                @Index(name = "idx_lessons_course_id", columnList = "course_id"),
                @Index(name = "idx_lessons_course_published", columnList = "course_id, published"),
                @Index(name = "idx_lessons_order_index", columnList = "course_id, order_index")
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
     * Đường dẫn URL tài liệu đính kèm, video bài giảng hoặc slide.
     */
    @Column(name = "content_url", length = 255)
    private String contentUrl;

    /**
     * Nội dung bài học dạng văn bản chi tiết hoặc hướng dẫn.
     */
    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    /**
     * Thứ tự sắp xếp hiển thị của bài học trong khóa học.
     */
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    /**
     * Trạng thái xuất bản: true = hiển thị tới sinh viên, false = bản nháp.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean published = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Khóa học chứa bài học này.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    // ================= HELPER METHODS =================

    public void addLessonProgress(LessonProgress progress) {
        this.lessonProgresses.add(progress);
        progress.setLesson(this);
    }

    public void removeLessonProgress(LessonProgress progress) {
        this.lessonProgresses.remove(progress);
        progress.setLesson(null);
    }
}