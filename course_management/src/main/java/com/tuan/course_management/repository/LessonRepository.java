package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface truy xuất dữ liệu bài học.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Page<Lesson> findByCourseIdAndPublishedTrueOrderByOrderIndexAsc(Long courseId, Pageable pageable);

    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    long countByCourseId(Long courseId);

    /**
     * Đếm tổng số bài học đã xuất bản trong khóa học (Phục vụ tính progress_percentage).
     */
    long countByCourseIdAndPublishedTrue(Long courseId);
}