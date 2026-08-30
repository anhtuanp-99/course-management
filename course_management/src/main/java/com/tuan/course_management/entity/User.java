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
 * Entity lưu thông tin tài khoản và vai trò người dùng trong hệ thống.
 */
@Entity
@Table(
        name = "users",
        indexes = {
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("Họ và tên đầy đủ")
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("Email dùng để đăng nhập (Unique - DB tự động tạo Unique Index ngầm)")
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
     * true: Active, false: Banned/Locked.
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

    /**
     * Danh sách khóa học do người dùng này làm giảng viên phụ trách.
     */
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    /**
     * Danh sách đăng ký khóa học của học viên.
     */
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Danh sách đánh giá khóa học do học viên viết.
     */
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}