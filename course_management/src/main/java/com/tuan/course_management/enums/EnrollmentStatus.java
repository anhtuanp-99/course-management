package com.tuan.course_management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum quản lý trạng thái tham gia khóa học của sinh viên.
 */
@Getter
@RequiredArgsConstructor
public enum EnrollmentStatus {

    ENROLLED("Đang học"),
    COMPLETED("Đã hoàn thành"),
    DROPPED("Hủy/Bỏ học");

    private final String description;
}