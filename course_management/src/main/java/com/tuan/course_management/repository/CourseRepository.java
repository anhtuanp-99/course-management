package com.tuan.course_management.repository;

import com.tuan.course_management.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    List<Course> findByTeacherId(Long teacherId);

    /**
     * Thống kê khóa học phổ biến nhất theo lượt đăng ký dạng phân trang.
     * Bổ sung countQuery để Spring Data JPA đếm tổng số trang chính xác khi có GROUP BY.
     */
    @Query(
            value = "SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c ORDER BY COUNT(e) DESC",
            countQuery = "SELECT COUNT(c) FROM Course c"
    )
    Page<Course> findTopCourses(Pageable pageable);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.teacher.id = :teacherId")
    long countTotalStudentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.course.teacher.id = :teacherId")
    Double calculateAvgRatingByTeacherId(@Param("teacherId") Long teacherId);

    long countByTeacherId(Long teacherId);
}