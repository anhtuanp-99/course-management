package com.tuan.course_management.exception;

/**
 * Ngoại lệ ném ra khi request không hợp lệ (dữ liệu sai, thiếu tham số...).
 * Dùng cho các lỗi nghiệp vụ do người dùng gửi lên.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}