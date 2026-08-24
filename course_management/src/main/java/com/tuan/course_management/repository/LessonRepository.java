package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Lấy danh sách bài học của một khóa học (có phân trang).
     */
    Page<Lesson> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Lấy danh sách bài học đã xuất bản của một khóa học.
     */
    List<Lesson> findByCourseIdAndIsPublishedTrue(Long courseId);

    /**
     * Đếm tổng số bài học trong một khóa học.
     */
    long countByCourseId(Long courseId);

    /**
     * Lấy số lượng bài học đã xuất bản trong một khóa học.
     */
    long countByCourseIdAndIsPublishedTrue(Long courseId);
}
