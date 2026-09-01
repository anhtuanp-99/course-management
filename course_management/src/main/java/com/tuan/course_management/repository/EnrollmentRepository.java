package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Enrollment;
import com.tuan.course_management.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface thao tác dữ liệu đăng ký khóa học.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    long countByCourseId(Long courseId);

    // Bổ sung hàm đếm tổng số khóa học học viên đăng ký
    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}