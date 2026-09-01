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

Đây là repo học tập, phục vụ mục đích thực hành cá nhân.