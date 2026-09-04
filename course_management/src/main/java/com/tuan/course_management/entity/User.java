package com.tuan.course_management.entity;

import com.tuan.course_management.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity lưu thông tin tài khoản và phân quyền người dùng (RBAC).
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_role", columnList = "role"),
                @Index(name = "idx_users_active", columnList = "active")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Comment("Tên tài khoản dùng để đăng nhập")
    private String username;

    @Column(name = "password_hash", nullable = false)
    @Comment("Mật khẩu đã được mã hóa BCrypt")
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("Email liên lạc")
    private String email;

    @Column(nullable = false, length = 100)
    @Comment("Họ và tên đầy đủ")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    @Comment("Vai trò người dùng (ADMIN, TEACHER, STUDENT)")
    private Role role = Role.STUDENT;

    @Column(length = 20)
    @Comment("Số điện thoại liên hệ")
    private String phone;

    @Builder.Default
    @Column(nullable = false)
    @Comment("Trạng thái hoạt động: true=Active, false=Banned")
    private boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("Thời điểm tạo tài khoản")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("Thời điểm cập nhật thông tin gần nhất")
    private LocalDateTime updatedAt;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}