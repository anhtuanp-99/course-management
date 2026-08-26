package com.tuan.course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity bài học.
 * Mỗi bài học thuộc về một khóa học, chứa nội dung bài giảng.
 * Chỉ những bài học đã publish mới hiển thị cho học viên.
 */
@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Nội dung đầy đủ của bài học.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Trạng thái publish: true = đã xuất bản, false = bản nháp.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean isPublished = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Khóa học chứa bài học này.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;

    /**
     * Danh sách tiến độ của học viên đối với bài học này.
     */
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<LessonProgress> lessonProgresses = new ArrayList<>();
}