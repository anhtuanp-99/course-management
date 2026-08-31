package com.tuan.course_management.service;

import com.tuan.course_management.dto.response.report.StudentProgressResponse;
import com.tuan.course_management.dto.response.report.TeacherOverviewResponse;
import com.tuan.course_management.dto.response.report.TopCourseResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Enrollment;
import com.tuan.course_management.entity.LessonProgress;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.EnrollmentRepository;
import com.tuan.course_management.repository.LessonProgressRepository;
import com.tuan.course_management.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Dịch vụ xử lý các báo cáo thống kê dành riêng cho ADMIN.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserService userService;

    /**
     * Báo cáo top khóa học phổ biến nhất theo số lượt đăng ký (Đáp ứng STT 37).
     */
    public List<TopCourseResponse> getTopCourses(int limit) {
        int safeLimit = limit > 0 ? limit : 10;
        log.debug("Lấy báo cáo top khóa học phổ biến, limit: {}", safeLimit);

        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            throw new AppException(ErrorCode.REPORT_DATA_NOT_FOUND);
        }

        return courses.stream()
                .map(course -> {
                    long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());
                    return TopCourseResponse.builder()
                            .courseId(course.getId())
                            .courseTitle(course.getTitle())
                            .enrollmentCount(enrollmentCount)
                            .averageRating(0.0)
                            .build();
                })
                .sorted((c1, c2) -> Long.compare(c2.getEnrollmentCount(), c1.getEnrollmentCount()))
                .limit(safeLimit)
                .toList();
    }

    /**
     * Báo cáo tiến độ học của một sinh viên cụ thể (Đáp ứng STT 38).
     */
    public StudentProgressResponse getStudentProgress(Long studentId) {
        log.debug("Lấy báo cáo tiến độ học của sinh viên ID: {}", studentId);

        User student = userService.getUserEntityById(studentId);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId, Pageable.unpaged()).getContent();
        if (enrollments.isEmpty()) {
            throw new AppException(ErrorCode.REPORT_DATA_NOT_FOUND);
        }

        List<StudentProgressResponse.CourseProgress> courseProgressList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            long totalLessons = lessonRepository.countByCourseId(course.getId());
            long completedLessons = lessonProgressRepository.findByEnrollmentId(enrollment.getId()).stream()
                    .filter(LessonProgress::isCompleted)
                    .count();

            double completionPercentage = totalLessons == 0
                    ? 0.0
                    : Math.round(((double) completedLessons / totalLessons) * 100.0 * 100.0) / 100.0;

            courseProgressList.add(StudentProgressResponse.CourseProgress.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .totalLessons((int) totalLessons)
                    .completedLessons((int) completedLessons)
                    .completionPercentage(completionPercentage)
                    .build());
        }

        return StudentProgressResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .courses(courseProgressList)
                .build();
    }

    /**
     * Báo cáo tổng quan khóa học của một giảng viên (Đáp ứng STT 39).
     */
    public TeacherOverviewResponse getTeacherOverview(Long teacherId) {
        log.debug("Lấy báo cáo tổng quan khóa học của giảng viên ID: {}", teacherId);

        User teacher = userService.getTeacherEntityById(teacherId);

        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        if (courses.isEmpty()) {
            throw new AppException(ErrorCode.REPORT_DATA_NOT_FOUND);
        }

        long totalEnrollments = 0;
        List<TeacherOverviewResponse.CourseSummary> summaries = new ArrayList<>();

        for (Course course : courses) {
            long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());
            long totalLessons = lessonRepository.countByCourseId(course.getId());

            totalEnrollments += enrollmentCount;

            summaries.add(TeacherOverviewResponse.CourseSummary.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .totalLessons((int) totalLessons)
                    .enrollmentCount(enrollmentCount)
                    .build());
        }

        return TeacherOverviewResponse.builder()
                .teacherId(teacher.getId())
                .teacherName(teacher.getFullName())
                .totalCourses(courses.size())
                .totalEnrollments(totalEnrollments)
                .courses(summaries)
                .build();
    }
}