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
 * Entity người dùng.
 * Lưu thông tin tài khoản và vai trò của người dùng trong hệ thống.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_email", columnList = "role")
})
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

    @Column(nullable = false, length = 100)
    @Comment("Họ và tên đầy đủ")
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("Email dùng để đăng nhập (Unique)")
    private String email;

    @Column(nullable = false)
    @Comment("Mật khẩu đã được mã hóa BCrypt")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("Vai trò người dùng trong hệ thống (STUDENT, TEACHER, ADMIN)")
    private Role role;

    @Column(length = 20)
    @Comment("Số điện thoại liên hệ")
    private String phone;

    /**
     * Trạng thái hoạt động của tài khoản.
     * true: Tài khoản đang hoạt động bình thường.
     * false: Tài khoản bị khóa (Admin banned hoặc chưa kích hoạt).
     */
    @Builder.Default
    @Column(nullable = false)
    @Comment("Trạng thái hoạt động: true=Active, false=Banned")
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    @Comment("Thời điểm tạo tài khoản")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("Thời điểm cập nhật thông tin gần nhất")
    private LocalDateTime updatedAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "teacher")
    private List<Course> courses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "student")
    private List<Review> reviews = new ArrayList<>();
}
