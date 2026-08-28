package com.tuan.course_management.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Tiện ích tạo Pageable từ tham số truyền vào (page, size, sortBy, sortDir).
 * Giúp chuẩn hóa cách phân trang và sắp xếp giữa các API.
 */
public class PageUtils {

    private PageUtils() {
        // Ngăn khởi tạo instance
    }

    /**
     * Tạo Pageable với tham số page, size và sắp xếp mặc định.
     *
     * @param page  số trang (bắt đầu từ 0)
     * @param size  kích thước trang
     * @param sortBy tên field sắp xếp (dùng mặc định nếu null/trống)
     * @param sortDir hướng sắp xếp: asc hoặc desc
     * @param defaultSortBy field sắp xếp mặc định nếu sortBy không hợp lệ
     */
    public static Pageable createPageable(int page, int size, String sortBy, String sortDir, String defaultSortBy) {
        // Giới hạn page >= 0
        int safePage = Math.max(page, 0);

        // Giới hạn size: mặc định 10, tối đa 100
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);

        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir != null && sortDir.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        // Xác định field sắp xếp, nếu không có thì dùng field mặc định
        String sortField = (sortBy == null || sortBy.isBlank()) ? defaultSortBy : sortBy;

        return PageRequest.of(safePage, safeSize, Sort.by(direction, sortField));
    }

    /**
     * Tạo Pageable đơn giản không sắp xếp.
     */
    public static Pageable createPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        return PageRequest.of(safePage, safeSize);
    }

    /**
     * Tạo Pageable với sắp xếp mặc định theo "createdAt" desc.
     */
    public static Pageable createPageableDefault(int page, int size) {
        return createPageable(page, size, "createdAt", "desc", "createdAt");
    }
}