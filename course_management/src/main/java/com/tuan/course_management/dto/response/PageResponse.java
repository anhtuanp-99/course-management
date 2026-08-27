package com.tuan.course_management.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO chứa dữ liệu phân trang.
 * Dùng chung cho tất cả API trả về danh sách có phân trang.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;      // Danh sách dữ liệu trang hiện tại
    private int page;            // Số trang hiện tại (0-based)
    private int size;            // Kích thước trang
    private long totalElements;  // Tổng số phần tử
    private int totalPages;      // Tổng số trang
    private boolean last;        // Có phải trang cuối không

    /**
     * Chuyển đổi từ đối tượng Page của Spring Data sang PageResponse.
     */
    public static <T> PageResponse<T> from(Page<T> pageData) {
        return PageResponse.<T>builder()
                .content(pageData.getContent())
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .last(pageData.isLast())
                .build();
    }
}