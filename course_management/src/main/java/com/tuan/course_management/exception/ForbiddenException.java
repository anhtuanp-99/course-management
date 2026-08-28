package com.tuan.course_management.exception;

/**
 * Ngoại lệ ném ra khi người dùng đã đăng nhập nhưng không có quyền truy cập.
 * Ví dụ: STUDENT cố gắng truy cập API của ADMIN.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}