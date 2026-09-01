package com.tuan.course_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonSummaryResponse {

    private Long id;
    private String title;
    private Integer orderIndex;
    private boolean published;
}