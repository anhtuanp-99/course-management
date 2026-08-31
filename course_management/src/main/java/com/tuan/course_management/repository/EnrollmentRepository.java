package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface thao tác cơ sở dữ liệu cho Entity Enrollment.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {

    /**
     * Lấy danh sách đăng ký khóa học của một học viên có phân trang (Đáp ứng STT 22).
     */
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Lấy danh sách lượt đăng ký theo khóa học có phân trang.
     */
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Đếm tổng số lượt đăng ký của một khóa học (Phục vụ ReportService - STT 37, 39).
     */
    long countByCourseId(Long courseId);

    /**
     * Kiểm tra học viên đã đăng ký khóa học hay chưa (Đáp ứng STT 23, 41).
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Truy vấn thông tin đăng ký cụ thể của học viên trong một khóa học.
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}