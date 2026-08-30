package com.tuan.course_management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum quản lý các trạng thái vòng đời của khóa học.
 */
@Getter
@RequiredArgsConstructor
public enum CourseStatus {

    DRAFT("Bản nháp"),
    PUBLISHED("Đã xuất bản"),
    ARCHIVED("Đã lưu trữ");

    private final String description;
}