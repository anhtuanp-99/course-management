package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
}