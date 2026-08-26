package com.tuan.course_management.repository;

import com.tuan.course_management.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository thao tác với bảng lesson_progress.
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    /**
     * Lấy danh sách tiến độ bài học của một enrollment (không cần phân trang).
     */
    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    /**
     * Kiểm tra xem một bài học đã được đánh dấu hoàn thành trong một enrollment chưa.
     */
    boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    /**
     * Tìm tiến độ của một bài học cụ thể trong một enrollment.
     */
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
}