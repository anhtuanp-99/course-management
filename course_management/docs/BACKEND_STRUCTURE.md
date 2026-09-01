# Cấu trúc Backend — Course Management

```
src/main/java/com/tuan/course_management/
│   CourseManagementApplication.java
│
├── config/
│       SecurityConfig.java
│
├── controller/
│       AuthController.java
│       CourseController.java
│       EnrollmentController.java
│       LessonController.java
│       NotificationController.java
│       ReportController.java
│       ReviewController.java
│       UserController.java
│
├── dto/
│   ├── request/
│   │       ChangePasswordRequest.java
│   │       CourseCreateRequest.java
│   │       CourseUpdateRequest.java
│   │       EnrollmentCreateRequest.java
│   │       LessonCreateRequest.java
│   │       LessonUpdateRequest.java
│   │       LoginRequest.java
│   │       NotificationCreateRequest.java
│   │       RefreshTokenRequest.java
│   │       RegisterRequest.java
│   │       ReviewCreateRequest.java
│   │       ReviewUpdateRequest.java
│   │       UpdateCourseStatusRequest.java
│   │       UpdateRoleRequest.java
│   │       UpdateStatusRequest.java
│   │       UserCreateRequest.java
│   │       UserUpdateRequest.java
│   │
│   └── response/
│           ApiResponse.java
│           AuthResponse.java
│           CourseDetailResponse.java
│           CourseResponse.java
│           CourseSummaryResponse.java
│           EnrollmentDetailResponse.java
│           EnrollmentResponse.java
│           JwtResponse.java
│           LessonProgressResponse.java
│           LessonResponse.java
│           LessonSummaryResponse.java
│           NotificationResponse.java
│           PageResponse.java
│           ReviewResponse.java
│           StudentProgressReportResponse.java
│           TeacherReportResponse.java
│           TopCourseResponse.java
│           UserResponse.java
│           UserSummaryResponse.java
│
├── entity/
│       Course.java
│       Enrollment.java
│       Lesson.java
│       LessonProgress.java
│       Notification.java
│       Review.java
│       User.java
│
├── enums/
│       CourseStatus.java
│       EnrollmentStatus.java
│       Role.java
│
├── exception/
│       AppException.java
│       ErrorCode.java
│       GlobalExceptionHandler.java
│
├── mapper/
│       CourseMapper.java
│       EnrollmentMapper.java
│       LessonMapper.java
│       LessonProgressMapper.java
│       NotificationMapper.java
│       ReviewMapper.java
│       UserMapper.java
│
├── repository/
│       CourseRepository.java
│       EnrollmentRepository.java
│       LessonProgressRepository.java
│       LessonRepository.java
│       NotificationRepository.java
│       ReviewRepository.java
│       UserRepository.java
│
├── security/
│       CustomUserDetailsService.java
│       JwtAccessDeniedHandler.java
│       JwtAuthenticationEntryPoint.java
│       JwtAuthenticationFilter.java
│       JwtProvider.java
│       UserPrincipal.java
│
├── service/
│       AuthService.java
│       CourseService.java
│       EnrollmentService.java
│       LessonService.java
│       NotificationService.java
│       ReportService.java
│       ReviewService.java
│       UserService.java
│
└── util/
        PageUtils.java
```

## Mô tả từng layer

| Package | Vai trò |
|---------|---------|
| `config` | Cấu hình Spring (Security config...) |
| `controller` | Tiếp nhận HTTP request, expose REST API |
| `dto/request` | Dữ liệu client gửi lên (input) |
| `dto/response` | Dữ liệu trả về cho client (output) |
| `entity` | Model ánh xạ bảng trong database (JPA Entity) |
| `enums` | Các giá trị enum dùng chung (trạng thái, vai trò...) |
| `exception` | Exception tùy chỉnh + xử lý lỗi tập trung (Global Exception Handler) |
| `mapper` | Chuyển đổi giữa Entity và DTO |
| `repository` | Truy vấn database (Spring Data JPA) |
| `security` | Xử lý xác thực/phân quyền bằng JWT |
| `service` | Chứa business logic |
| `util` | Các hàm tiện ích dùng chung |

## Thống kê

| Package | Số file |
|---------|---------|
| config | 1 |
| controller | 8 |
| dto/request | 16 |
| dto/response | 18 |
| entity | 7 |
| enums | 3 |
| exception | 3 |
| mapper | 7 |
| repository | 7 |
| security | 6 |
| service | 8 |
| util | 1 |
