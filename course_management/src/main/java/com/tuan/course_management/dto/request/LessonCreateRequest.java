package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCreateRequest {

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    @Size(max = 200, message = "Tiêu đề bài học không quá 200 ký tự")
    private String title;

    @Size(max = 255, message = "Đường dẫn tài liệu/video không quá 255 ký tự")
    private String contentUrl;

    private String textContent;

    @NotNull(message = "Thứ tự bài học không được để trống")
    @Min(value = 1, message = "Thứ tự bài học phải bắt đầu từ 1")
    private Integer orderIndex;

    @Builder.Default
    private boolean published = false;
}