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
     * Factory method tạo UserPrincipal từ Entity User.
     */
    public static UserPrincipal create(User user) {
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

    /**
     * Tương thích với đặt tên from(user) nếu cần.
     */
    public static UserPrincipal from(User user) {
        return create(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Đảm bảo tiền tố ROLE_ để hoạt động đúng với @PreAuthorize("hasRole('ADMIN')")
        String roleName = role.name().startsWith("ROLE_") ? role.name() : "ROLE_" + role.name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return password;
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