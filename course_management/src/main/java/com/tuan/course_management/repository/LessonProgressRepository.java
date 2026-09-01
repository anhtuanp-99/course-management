package com.tuan.course_management.repository;

import com.tuan.course_management.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface thao tác dữ liệu tiến độ bài học.
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    /**
     * Đếm tổng số bài học sinh viên đã hoàn thành trong lượt đăng ký (Phục vụ tính progress_percentage).
     */
    long countByEnrollmentIdAndCompletedTrue(Long enrollmentId);
}