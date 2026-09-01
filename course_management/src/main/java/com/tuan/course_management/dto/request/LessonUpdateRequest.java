package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonUpdateRequest {

    @Size(max = 200, message = "Tiêu đề bài học không quá 200 ký tự")
    private String title;

    @Size(max = 255, message = "Đường dẫn tài liệu/video không quá 255 ký tự")
    private String contentUrl;

    private String textContent;

    @Min(value = 1, message = "Thứ tự bài học phải bắt đầu từ 1")
    private Integer orderIndex;

    /**
     * Sử dụng wrapper class Boolean cho thao tác PATCH/PUT
     * để phân biệt giữa NULL (giữ nguyên) và false (chuyển về nháp).
     */
    private Boolean published;
}