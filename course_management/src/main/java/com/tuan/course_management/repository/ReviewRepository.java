package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Lấy đánh giá của một student cho một khóa học cụ thể.
     */
    Optional<Review> findByCourseIdAndStudentId(Long courseId, Long studentId);

    /**
     * Lấy danh sách đánh giá của một khóa học (có phân trang).
     */
    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Lấy danh sách đánh giá của một student (có phân trang).
     */
    Page<Review> findByStudentId(Long studentId, Pageable pageable);

    /**
     * Đếm số đánh giá của một khóa học.
     */
    long countByCourseId(Long courseId);

    /**
     * Tính trung bình rating của một khóa học.
     */
    Double avgRatingByCourseId(Long courseId);
}
