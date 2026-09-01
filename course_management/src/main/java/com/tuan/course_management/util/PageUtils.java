package com.tuan.course_management.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility tạo Pageable chuẩn hóa cho toàn hệ thống.
 * Tự động chuyển đổi page từ 1-based (Client API) sang 0-based (Spring Data JPA).
 */
public class PageUtils {

    private PageUtils() {
        // Prevent instantiation
    }

    /**
     * Tạo Pageable với Sort đã được validate sẵn ở Service.
     */
    public static Pageable createPageable(int page, int size, Sort sort) {
        // Chuyển 1-based page từ Controller thành 0-based cho JPA
        int safePage = Math.max(0, page - 1);

        // Giới hạn size: mặc định 10 nếu <= 0, tối đa 100 để tránh ngốn RAM DB
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);

        return PageRequest.of(safePage, safeSize, sort != null ? sort : Sort.unsorted());
    }

    /**
     * Tạo Pageable không sắp xếp.
     */
    public static Pageable createPageable(int page, int size) {
        return createPageable(page, size, Sort.unsorted());
    }
}