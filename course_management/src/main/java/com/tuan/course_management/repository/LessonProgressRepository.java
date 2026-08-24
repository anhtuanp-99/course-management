package com.tuan.course_management.repository;

import com.tuan.course_management.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    /**
     * Lấy tiến độ của một bài học trong một đăng ký cụ thể.
     */
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    /**
     * Lấy tất cả tiến độ của một đăng ký.
     */
    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    /**
     * Đếm số bài đã hoàn thành trong một đăng ký.
     */
    long countByEnrollmentIdAndIsCompletedTrue(Long enrollmentId);

    /**
     * Đếm số lượng bài học đã được khởi tạo tiến độ trong một lượt đăng ký.
     */
    long countByEnrollmentId(Long enrollmentId);

}
