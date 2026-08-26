package com.tuan.course_management.security;

import com.tuan.course_management.entity.User;
import com.tuan.course_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService – Cầu nối giữa Database và Spring Security.
 * Lý do tạo: Spring Security không biết cấu trúc bảng User, cần một service để tải user từ DB.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("Tải user từ DB với email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Không tìm thấy user với email: {}", email);
                    return new UsernameNotFoundException("Không tìm thấy user với email: " + email);
                });

        log.debug("Tải user thành công: {}", user.getEmail());
        return UserPrincipal.create(user);
    }


}
