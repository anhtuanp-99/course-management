package com.tuan.course_management.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String fullName;
    private String phone;

    /**
     * Dùng wrapper class Boolean cho thao tác Cập nhật một phần (Partial Update / PATCH)
     * để phân biệt giữa NULL (không cập nhật) và false (vô hiệu hóa).
     */
    private Boolean active;
}