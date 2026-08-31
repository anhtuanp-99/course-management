package com.tuan.course_management.repository;

import com.tuan.course_management.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface thao tác dữ liệu tiến độ bài học (LessonProgress).
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    /**
     * Lấy danh sách tiến độ bài học thuộc đợt đăng ký (STT 24).
     */
    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    /**
     * Tìm thông tin tiến độ của một bài học cụ thể trong đợt đăng ký (STT 25).
     */
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
}