package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Course;
import com.tuan.course_management.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository thao tác với bảng courses.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    /**
     * Lấy danh sách khóa học theo trạng thái, có phân trang.
     */
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    /**
     * Lấy danh sách khóa học theo giảng viên phụ trách, có phân trang.
     */
    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);

    /**
     * Lấy danh sách khóa học theo giảng viên và trạng thái, có phân trang.
     */
    Page<Course> findByTeacherIdAndStatus(Long teacherId, CourseStatus status, Pageable pageable);
}