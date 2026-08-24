package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Lấy đăng ký của một student trong một khóa học cụ thể.
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Lấy danh sách đăng ký của một student (có phân trang).
     */
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Lấy danh sách đăng ký của một khóa học (có phân trang).
     */
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Đếm số lượng student đã đăng ký một khóa học.
     */
    long countByCourseId(Long courseId);

}
