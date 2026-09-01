package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository thao tác dữ liệu đánh giá khóa học.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    Page<Review> findByStudentId(Long studentId, Pageable pageable);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Tính điểm đánh giá trung bình (sao) của một khóa học cụ thể.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.course.id = :courseId")
    Double calculateAvgRatingByCourseId(@Param("courseId") Long courseId);

    /**
     * Đếm tổng số lượt đánh giá của một khóa học.
     */
    long countByCourseId(Long courseId);
}