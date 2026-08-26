package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository thao tác với bảng enrollments.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {

    /**
     * Lấy danh sách đăng ký của một học viên, có phân trang.
     */
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Lấy danh sách học viên đã đăng ký một khóa học, có phân trang.
     */
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Kiểm tra học viên đã đăng ký khóa học hay chưa.
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Tìm đăng ký của học viên với khóa học cụ thể.
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}