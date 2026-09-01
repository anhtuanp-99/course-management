package com.tuan.course_management.security;

import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Lớp đại diện cho thông tin người dùng được Spring Security xác thực.
 * Chuyển đổi dữ liệu từ Entity User sang đối tượng UserDetails.
 */
@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String fullName;
    private final String password;
    private final Role role;
    private final boolean active;

    /**
     * Khởi tạo đối tượng UserPrincipal từ Entity User.
     *
     * @param user Entity người dùng
     * @return UserPrincipal Đối tượng chứa thông tin xác thực
     */
    public static UserPrincipal from(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .password(user.getPassword())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}