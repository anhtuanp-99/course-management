package com.tuan.course_management.exception;

/**
 * Ngoại lệ ném ra khi có xung đột dữ liệu (unique constraint, trùng lặp).
 * Ví dụ: đăng ký khóa học đã đăng ký, gửi đánh giá trùng lặp.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}