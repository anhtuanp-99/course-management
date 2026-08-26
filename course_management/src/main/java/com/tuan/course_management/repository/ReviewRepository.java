package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository thao tác với bảng reviews.
 * - JpaRepository: cung cấp CRUD và phân trang.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Lấy danh sách đánh giá của một khóa học, có phân trang.
     */
    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Lấy danh sách đánh giá của một học viên, có phân trang.
     */
    Page<Review> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Kiểm tra học viên đã đánh giá khóa học hay chưa.
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Tìm đánh giá của học viên đối với khóa học cụ thể.
     */
    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);
}