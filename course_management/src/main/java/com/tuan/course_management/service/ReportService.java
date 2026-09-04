package com.tuan.course_management.service;

import com.tuan.course_management.dto.response.CourseSummaryResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.StudentProgressReportResponse;
import com.tuan.course_management.dto.response.TeacherReportResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.EnrollmentStatus;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.CourseMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.EnrollmentRepository;
import com.tuan.course_management.repository.ReviewRepository;
import com.tuan.course_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;

    /**
     * Báo cáo danh sách top các khóa học phổ biến nhất trả về định dạng phân trang PageResponse (STT 37).
     */
    public PageResponse<CourseSummaryResponse> getTopCourses(int page, int size) {
        log.debug("Truy vấn báo cáo top khóa học phổ biến - Page: {}, Size: {}", page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<Course> coursePage = courseRepository.findTopCourses(pageable);

        List<CourseSummaryResponse> mappedContent = coursePage.getContent().stream()
                .map(course -> {
                    Double avgRating = reviewRepository.calculateAvgRatingByCourseId(course.getId());
                    long totalStudents = enrollmentRepository.countByCourseId(course.getId());
                    return courseMapper.toSummaryResponse(course, avgRating, totalStudents);
                })
                .toList();

        return PageResponse.from(coursePage, mappedContent);
    }

    // STT 38
    public StudentProgressReportResponse getStudentProgressReport(Long studentId) {
        // 1. Kiểm tra học viên có tồn tại hay không
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Thống kê số lượng đăng ký
        long totalEnrolled = enrollmentRepository.countByStudentId(studentId);
        long completed = enrollmentRepository.countByStudentIdAndStatus(studentId, EnrollmentStatus.COMPLETED);

        // 3. Tính tỷ lệ % hoàn thành (Làm tròn 2 chữ số thập phân)
        double completionRate = 0.0;
        if (totalEnrolled > 0) {
            double rawRate = ((double) completed / totalEnrolled) * 100;
            completionRate = BigDecimal.valueOf(rawRate)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return StudentProgressReportResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .email(student.getEmail())
                .totalEnrolledCourses(totalEnrolled)
                .completedCourses(completed)
                .overallCompletionRate(completionRate)
                .build();
    }

    /**
     * Báo cáo tổng quan khóa học và đánh giá của giảng viên (STT 39).
     */
    public TeacherReportResponse getTeacherOverview(Long teacherId) {
        log.debug("Truy vấn báo cáo tổng quan cho Teacher ID: {}", teacherId);

        User teacher = userService.getTeacherEntityById(teacherId);

        long totalCourses = courseRepository.countByTeacherId(teacherId);
        long totalStudents = courseRepository.countTotalStudentsByTeacherId(teacherId);
        Double avgRating = courseRepository.calculateAvgRatingByTeacherId(teacherId);

        return TeacherReportResponse.builder()
                .teacherId(teacher.getId())
                .teacherName(teacher.getFullName())
                .totalCourses(totalCourses)
                .totalStudents(totalStudents)
                .avgRating(avgRating != null ? avgRating : 0.0)
                .build();
    }
}