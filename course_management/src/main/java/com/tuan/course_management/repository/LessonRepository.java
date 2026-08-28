package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository thao tác với bảng lessons.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    /**
     * Lấy danh sách bài học của một khóa học, có phân trang.
     */
    Page<Lesson> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Lấy danh sách bài học đã publish của một khóa học, có phân trang.
     */
    Page<Lesson> findByCourseIdAndPublishedTrue(Long courseId, Pageable pageable);
}