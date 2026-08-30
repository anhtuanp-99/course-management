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

    /**
     * Lấy danh sách bài học đã xuất bản của một khóa học (STT 11, 16).
     */
    Page<Lesson> findByCourseIdAndPublishedTrue(Long courseId, Pageable pageable);

    /**
     * Lấy toàn bộ bài học thuộc một khóa học dành cho Giảng viên / ADMIN (STT 16).
     */
    List<Lesson> findByCourseId(Long courseId);

    /**
     * Đếm tổng số bài học của một khóa học.
     */
    long countByCourseId(Long courseId);
}