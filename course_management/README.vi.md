[English](README.md) | **Tiếng Việt**

# Course Management

> Backend API quản lý khóa học trực tuyến, xây dựng bằng Spring Boot — hỗ trợ xác thực JWT, quản lý khóa học, bài học, ghi danh, đánh giá, thông báo và báo cáo.
>
> 📚 Đây là project cá nhân mang tính **học tập / luyện tập**, viết để thực hành thiết kế REST API, kiến trúc layered với Spring Boot và JWT authentication.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)

## Mục lục

- [Giới thiệu](#giới-thiệu)
- [Tính năng](#tính-năng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt & Chạy](#cài-đặt--chạy)
- [Cấu hình](#cấu-hình)
- [API Endpoints](#api-endpoints)
- [Kỹ thuật & Nguyên lý áp dụng](#kỹ-thuật--nguyên-lý-áp-dụng)
- [Testing](#testing)
- [Tài liệu](#tài-liệu)
- [Ghi chú](#ghi-chú)

## Giới thiệu

**Course Management** là hệ thống backend cho nền tảng quản lý khóa học trực tuyến, cung cấp REST API cho việc đăng ký/đăng nhập, tạo và quản lý khóa học, bài học, tiến độ học tập, đánh giá khóa học, thông báo và báo cáo thống kê. Dự án được thiết kế theo kiến trúc layered (Controller – Service – Repository) chuẩn Spring Boot, có tài liệu SRS và ERD đi kèm trong thư mục `docs/`.

## Tính năng

- Xác thực & phân quyền người dùng bằng JWT (access token + refresh token)
- Quản lý khóa học (CRUD khóa học)
- Quản lý bài học và tiến độ học của học viên
- Ghi danh khóa học (Enrollment)
- Đánh giá khóa học (Review)
- Thông báo (Notification)
- Báo cáo thống kê (Report)
- Validate dữ liệu đầu vào, xử lý exception tập trung

## Công nghệ sử dụng

- **Java 21**
- **Spring Boot 3.2.5** (Web, Data JPA, Security, Validation)
- **PostgreSQL**
- **JJWT 0.12.6** — xử lý JSON Web Token
- **Lombok**
- **Gradle** (build tool, dùng Gradle Wrapper)
- **JUnit 5 / Spring Security Test** — testing

## Cấu trúc thư mục

```
course_management/
├── docs/                          # SRS, ERD, tài liệu thiết kế
├── src/main/java/com/tuan/course_management/
│   ├── config/                    # Cấu hình Spring (Security, Bean...)
│   ├── controller/                # REST controllers
│   ├── dto/
│   │   ├── request/                # DTO cho request
│   │   └── response/               # DTO cho response
│   ├── entity/                    # JPA entities
│   ├── enums/                     # Enum dùng chung
│   ├── exception/                 # Custom exception & xử lý lỗi tập trung
│   ├── mapper/                    # Mapper giữa entity <-> DTO
│   ├── repository/                # Spring Data JPA repositories
│   ├── security/                  # JWT filter, security config
│   ├── service/                   # Business logic
│   └── util/                      # Utility classes
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
└── src/test/                      # Unit test / integration test
```

## Yêu cầu hệ thống

- JDK 21+
- PostgreSQL 14+ (hoặc mới hơn)
- Không cần cài Gradle riêng — dự án dùng Gradle Wrapper (`gradlew`)

## Cài đặt & Chạy

1. Clone repository

```bash
git clone https://github.com/anhtuanp-99/course-management.git
cd course-management/course_management
```

2. Tạo database PostgreSQL

```sql
CREATE DATABASE course_management_db;
```

3. Cấu hình kết nối DB và JWT secret (xem phần [Cấu hình](#cấu-hình))

4. Chạy ứng dụng (mặc định dùng profile `dev`)

```bash
./gradlew bootRun
```

Ứng dụng sẽ chạy tại `http://localhost:8080`

## Cấu hình

Project dùng Spring Profiles (`application-dev.properties`, `application-prod.properties`). Một số biến quan trọng cần cấu hình:

| Biến | Mô tả | Ví dụ (dev) |
|------|-------|-------------|
| `spring.datasource.url` | URL kết nối PostgreSQL | `jdbc:postgresql://localhost:5432/course_management_db` |
| `spring.datasource.username` / `password` | Thông tin đăng nhập DB | — |
| `JWT_SECRET` | Secret key để ký JWT | cấu hình qua biến môi trường |
| `jwt.expiration` | Thời gian hết hạn access token (ms) | `86400000` |
| `jwt.refresh-expiration` | Thời gian hết hạn refresh token (ms) | `604800000` |

> Profile mặc định đang active là `dev,local` (`spring.profiles.active`) — chỉnh trong `application.properties` khi deploy production.

## API Endpoints

Tất cả API đều có tiền tố `/api/v1`.

| Nhóm | Base path | Controller |
|------|-----------|------------|
| Xác thực | `/api/v1/auth` | `AuthController` |
| Người dùng | `/api/v1/users` | `UserController` |
| Khóa học | `/api/v1/courses` | `CourseController` |
| Bài học | `/api/v1` | `LessonController` |
| Ghi danh | `/api/v1/enrollments` | `EnrollmentController` |
| Đánh giá | `/api/v1` | `ReviewController` |
| Thông báo | `/api/v1/notifications` | `NotificationController` |
| Báo cáo | `/api/v1/reports` | `ReportController` |

> Chi tiết từng endpoint (method, request/response body) nên bổ sung bằng Swagger/OpenAPI hoặc Postman collection để dễ tra cứu hơn.

## Kỹ thuật & Nguyên lý áp dụng

### Database Indexing

Các entity được đánh index tại những cột thường xuyên dùng để join hoặc lọc dữ liệu, giúp tăng tốc truy vấn:

| Bảng | Index | Mục đích |
|------|-------|----------|
| `users` | `idx_users_username` | Tăng tốc tra cứu khi đăng nhập |
| `courses` | `idx_courses_teacher_id` | Tăng tốc lọc khóa học theo giảng viên |
| `lessons` | `idx_lessons_course_id` | Tăng tốc lấy danh sách bài học theo khóa học |
| `notifications` | `idx_notifications_user_id` | Tăng tốc lấy thông báo theo người dùng |

Ngoài ra, các ràng buộc nghiệp vụ quan trọng được đảm bảo bằng `@UniqueConstraint` ở tầng database (không chỉ validate ở tầng service):

- `enrollments`: `UNIQUE(student_id, course_id)` — 1 sinh viên chỉ đăng ký 1 khóa học 1 lần
- `lesson_progress`: `UNIQUE(enrollment_id, lesson_id)` — 1 bài học chỉ có 1 bản ghi tiến độ trên mỗi lượt đăng ký
- `reviews`: `UNIQUE(course_id, student_id)` — 1 sinh viên chỉ đánh giá 1 lần cho mỗi khóa học

### Transaction Management (ACID)

Các service dùng `@Transactional` của Spring để đảm bảo tính toàn vẹn dữ liệu:

- `@Transactional(readOnly = true)` ở method đọc dữ liệu (tối ưu hiệu năng, tránh khóa không cần thiết)
- `@Transactional` ở method ghi/cập nhật — đảm bảo **Atomicity**: nếu một thao tác gồm nhiều bước ghi DB (VD: đăng ký khóa học + khởi tạo tiến độ học) thất bại giữa chừng, toàn bộ sẽ rollback thay vì để dữ liệu ở trạng thái nửa vời

### SOLID — và một đánh đổi có chủ đích

Project áp dụng một phần nguyên lý SOLID:

- **Single Responsibility**: tách rõ theo layer — Controller (nhận request) / Service (business logic) / Repository (truy vấn DB) / Mapper (chuyển đổi Entity ↔ DTO)
- **Dependency Injection**: constructor injection qua `@RequiredArgsConstructor` (Lombok) thay vì field injection `@Autowired`, giúp dễ test và code rõ ràng hơn

**Điểm chưa áp dụng, có chủ đích:** các Service (`CourseService`, `UserService`...) hiện không tách interface riêng (kiểu `ICourseService` + `CourseServiceImpl`) — tức là chưa dùng **Dependency Inversion Principle (DIP)** một cách đầy đủ. Đây là lựa chọn có cân nhắc trong giai đoạn phát triển một mình: mỗi service chỉ có một implementation duy nhất, và việc thêm interface lúc này chỉ tạo thêm boilerplate mà chưa mang lại lợi ích thực tế. Sẽ cân nhắc tách interface nếu sau này cần viết unit test kỹ hơn (mock qua interface sạch hơn) hoặc có nhiều implementation cho cùng một service.

## Testing

```bash
./gradlew test
```

## Tài liệu

Thư mục `docs/` chứa:
- `SRS - Quản lý khóa học.pdf` — Tài liệu đặc tả yêu cầu phần mềm
- `ERD.png` — Sơ đồ thực thể quan hệ (Entity Relationship Diagram)
- `backend-architecture.md` — Mô tả kiến trúc backend
- Khung chức năng chi tiết (file PDF)

## Ghi chú

Đây là repo học tập, được xây dựng để thực hành thiết kế và triển khai một hệ thống backend hoàn chỉnh theo kiến trúc RESTful.
