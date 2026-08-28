package com.tuan.course_management.exception;

/**
 * Ngoại lệ ném ra khi không tìm thấy tài nguyên (entity) theo ID.
 * Thường dùng cho các trường hợp GET, PUT, DELETE theo id không tồn tại.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}