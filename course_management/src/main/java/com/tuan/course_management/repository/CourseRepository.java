package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface truy xuất dữ liệu liên quan đến khóa học trong cơ sở dữ liệu.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    /**
     * Lấy danh sách khóa học do một giảng viên phụ trách (Phục vụ ReportService - STT 39).
     */
    List<Course> findByTeacherId(Long teacherId);

    /**
     * Lấy danh sách khóa học phổ biến nhất dựa trên số lượt đăng ký (Phục vụ API Báo cáo STT 37).
     */
    @Query("SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c ORDER BY COUNT(e) DESC")
    List<Course> findTopCourses(Pageable pageable);

    /**
     * Đếm tổng số lượt đăng ký học viên cho tất cả các khóa học của một giảng viên (STT 39).
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.teacher.id = :teacherId")
    long countTotalStudentsByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Tính điểm đánh giá trung bình của tất cả các khóa học do giảng viên phụ trách (STT 39).
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.course.teacher.id = :teacherId")
    Double calculateAvgRatingByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Đếm số lượng khóa học thuộc sở hữu của một giảng viên.
     */
    long countByTeacherId(Long teacherId);
}