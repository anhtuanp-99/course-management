package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Lấy danh sách khóa học có phân trang, lọc theo status.
     */
    Page<Course> findByStatus(String status, Pageable pageable);

    /**
     * Lấy danh sách khóa học của một giảng viên (có phân trang).
     */
    Page<Course> findByTeacherId(Long teacherid, Pageable pageable);

    /**
     * Tìm kiếm khóa học theo từ khóa trong title hoặc description (có phân trang).
     */
    @Query("""
            SELECT c FROM Course c
            WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Course> searchByKeyWord(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy danh sách khóa học phổ biến (theo số lượng đăng ký) – dùng cho báo cáo.
     */
    @Query("""
        SELECT c FROM Course c
        LEFT JOIN c.enrollments e
        GROUP BY c
        ORDER BY COUNT(e) DESC
    """)
    List<Course> findTopCourses(Pageable pageable);
}
