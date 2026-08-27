package com.tuan.course_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Response chuẩn cho tất cả API.
 * - success: trạng thái thành công hay thất bại.
 * - message: thông điệp mô tả (thường dùng cho lỗi).
 * - data: dữ liệu trả về (có thể là object, list, page...).
 * - JsonInclude NON_NULL để không hiển thị field null.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Factory method tạo response thành công
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // Factory method tạo response thất bại
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}