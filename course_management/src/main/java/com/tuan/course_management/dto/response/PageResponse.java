package com.tuan.course_management.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper đóng gói dữ liệu phân trang chuẩn hóa cho toàn bộ API hệ thống.
 *
 * @param <T> Kiểu dữ liệu của danh sách phần tử trong trang
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    /**
     * Helper method tạo PageResponse từ Spring Data Page và danh sách DTO đã map.
     */
    public static <T> PageResponse<T> from(Page<?> page, List<T> mappedContent) {
        return PageResponse.<T>builder()
                .content(mappedContent)
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}