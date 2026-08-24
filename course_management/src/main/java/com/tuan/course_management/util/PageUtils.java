package com.tuan.course_management.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;

import javax.sound.midi.MidiFileFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * PageUtils – tiện ích tạo Pageable từ các tham số.
 * Lý do tạo: Tránh lặp code tạo Pageable trong Service.
 */
public class PageUtils {

    /**
     * Tạo Pageable với sắp xếp mặc định theo id giảm dần.
     */
    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("id").descending());
    }

    /**
     * Tạo Pageable với sắp xếp tùy chỉnh.
     * @param sortBy tên trường sắp xếp (VD: "title")
     * @param direction "ASC" hoặc "DESC"
     */
    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort.Direction dir = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(dir, sortBy));
    }

    /**
     * Tạo Pageable với nhiều trường sắp xếp.
     * @param sort chuỗi định dạng: "field1,asc|field2,desc"
     */
    public static Pageable createPageable(int page, int size, String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return PageRequest.of(page, size);
        }

        List<Sort.Order> orders = new ArrayList<>();
        String[] sortFields = sort.split("\\|");

        for (String fieldInfo : sortFields) {
            String[] parts = fieldInfo.split(",");
            String property = parts[0].trim();

            if (property.isEmpty()) continue;

            Sort.Direction direction = Sort.Direction.DESC;
            if (parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")) {
                direction = Sort.Direction.ASC;
            }

            orders.add(new Sort.Order(direction, property));
        }

        return orders.isEmpty()
                ? PageRequest.of(page, size)
                : PageRequest.of(page, size, Sort.by(orders));
    }


}
