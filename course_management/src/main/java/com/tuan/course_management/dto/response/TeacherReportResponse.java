package com.tuan.course_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherReportResponse {

    private Long teacherId;
    private String teacherName;
    private long totalCourses;
    private long totalStudents;
    private Double avgRating;
}