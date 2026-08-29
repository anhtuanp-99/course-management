package com.tuan.course_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Quản lý tập trung toàn bộ mã lỗi, thông báo và HTTP Status của hệ thống.
 */
@Getter
public enum ErrorCode {

    // Auth & User Errors
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1002, "Email đã được sử dụng", HttpStatus.CONFLICT),
    INVALID_PASSWORD(1003, "Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS(1004, "Bạn cần đăng nhập để truy cập", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_RESOURCE(1005, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),

    // Course Errors
    COURSE_NOT_FOUND(2001, "Không tìm thấy khóa học", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PUBLISHED(2002, "Khóa học đã được xuất bản", HttpStatus.BAD_REQUEST),

    // System Errors
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống chưa được phân loại", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}