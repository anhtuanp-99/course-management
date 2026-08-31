# Course Management System

🇬🇧 [English version](README.en.md)

Hệ thống quản lý khóa học trực tuyến được xây dựng bằng **Spring Boot**, hỗ trợ phân quyền theo vai trò (Admin, Teacher,
Student) cho các nghiệp vụ quản lý khóa học, bài học, đăng ký học, đánh giá và thông báo.

## Mục lục

- [Tính năng chính](#tính-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt và chạy dự án](#cài-đặt-và-chạy-dự-án)
- [Cấu hình](#cấu-hình)
- [Phân quyền](#phân-quyền)
- [API Endpoints](#api-endpoints)
- [Kiểm thử API với Postman](#kiểm-thử-api-với-postman)

## Tính năng chính

- Xác thực và phân quyền người dùng bằng JWT (Access Token)
- Quản lý người dùng: tạo, cập nhật vai trò, kích hoạt/vô hiệu hóa, xóa
- Quản lý khóa học: tạo, cập nhật, đổi trạng thái (DRAFT, PUBLISHED, ARCHIVED), xóa
- Quản lý bài học trong từng khóa học, ẩn/hiện nội dung theo trạng thái xuất bản
- Đăng ký khóa học và theo dõi tiến độ học tập theo từng bài học
- Đánh giá/bình luận khóa học
- Thông báo hệ thống cho người dùng
- Báo cáo thống kê: khóa học phổ biến, tiến độ học viên, tổng quan giảng viên
- Tìm kiếm và lọc khóa học theo từ khóa, giảng viên, trạng thái

## Công nghệ sử dụng

| Thành phần        | Công nghệ             |
|-------------------|-----------------------|
| Ngôn ngữ          | Java                  |
| Framework         | Spring Boot           |
| Bảo mật           | Spring Security + JWT |
| Truy xuất dữ liệu | Spring Data JPA       |
| Cơ sở dữ liệu     | PostgreSQL            |
| Build tool        | Gradle                |
| Kiểm thử API      | Postman               |

## Cấu trúc thư mục

```
src/main/java/com/tuan/course_management
├── config              # Cấu hình Spring Security, khởi tạo dữ liệu mặc định
├── controller           # REST controllers
├── dto
│   ├── request           # Request payload
│   └── response          # Response payload (bao gồm response cho báo cáo)
├── entity                # JPA entities
├── enums                 # Enum: Role, CourseStatus
├── exception             # Xử lý exception tập trung, mã lỗi
├── mapper                # Chuyển đổi entity <-> DTO
├── repository            # Spring Data JPA repositories
├── security              # JWT provider, filter, UserDetails, xử lý lỗi xác thực
├── service                # Business logic
└── util                   # Tiện ích dùng chung (phân trang,...)

src/main/resources
├── application.properties
├── application-dev.properties
├── application-local.properties
├── application-prod.properties
└── logback-spring.xml
```

## Yêu cầu hệ thống

- JDK 17 trở lên
- PostgreSQL 13 trở lên
- Gradle (có thể dùng Gradle Wrapper đi kèm project, không cần cài riêng)

## Cài đặt và chạy dự án

1. Clone dự án về máy:

```bash
git clone <repository-url>
cd course_management
```

2. Tạo database PostgreSQL:

```sql
CREATE
DATABASE course_management;
```

3. Cấu hình kết nối database trong `src/main/resources/application-local.properties` (hoặc profile bạn sử dụng):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/course_management
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>
```

4. Build project:

```bash
# Windows
.\gradlew.bat build

# Linux/Mac
./gradlew build
```

5. Chạy ứng dụng:

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

Mặc định ứng dụng chạy tại `http://localhost:8080`.

## Cấu hình

Dự án hỗ trợ nhiều profile:

| Profile | Mục đích                      |
|---------|-------------------------------|
| local   | Chạy trên máy cá nhân         |
| dev     | Môi trường phát triển         |
| prod    | Môi trường triển khai thực tế |

Chọn profile khi chạy:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Phân quyền

Hệ thống sử dụng JWT kèm các vai trò sau:

| Vai trò | Mô tả                                                      |
|---------|------------------------------------------------------------|
| ADMIN   | Toàn quyền quản trị hệ thống                               |
| TEACHER | Quản lý khóa học và bài học được phân công                 |
| STUDENT | Đăng ký khóa học, học và đánh giá                          |
| OWNER   | Chủ sở hữu tài nguyên (tự quản lý hồ sơ/đánh giá của mình) |

Client cần gửi token trong header của các request cần xác thực:

```
Authorization: Bearer <access_token>
```

## API Endpoints

### Auth

| Method | Endpoint         | Quyền  | Mô tả                             |
|--------|------------------|--------|-----------------------------------|
| POST   | /api/auth/login  | PUBLIC | Đăng nhập, nhận JWT               |
| POST   | /api/auth/verify | AUTH   | Xác thực token                    |
| GET    | /api/auth/me     | AUTH   | Lấy thông tin người dùng hiện tại |
| POST   | /api/auth/logout | AUTH   | Đăng xuất                         |

### Users

| Method | Endpoint                      | Quyền        | Mô tả                           |
|--------|-------------------------------|--------------|---------------------------------|
| GET    | /api/users                    | ADMIN        | Danh sách người dùng            |
| GET    | /api/users?status={status}    | ADMIN        | Lọc theo trạng thái             |
| GET    | /api/users/{user_id}          | ADMIN        | Chi tiết người dùng             |
| POST   | /api/users                    | ADMIN        | Tạo người dùng mới              |
| PUT    | /api/users/{user_id}          | OWNER, ADMIN | Cập nhật thông tin cá nhân      |
| PUT    | /api/users/{user_id}/role     | ADMIN        | Cập nhật vai trò                |
| PUT    | /api/users/{user_id}/status   | ADMIN        | Kích hoạt/vô hiệu hóa tài khoản |
| PUT    | /api/users/{user_id}/password | OWNER, ADMIN | Đổi mật khẩu                    |
| DELETE | /api/users/{user_id}          | ADMIN        | Xóa người dùng                  |

### Courses

| Method | Endpoint                             | Quyền | Mô tả                        |
|--------|--------------------------------------|-------|------------------------------|
| GET    | /api/courses                         | AUTH  | Danh sách khóa học           |
| GET    | /api/courses?search={keyword}        | AUTH  | Tìm kiếm theo từ khóa        |
| GET    | /api/courses?teacher_id={teacher_id} | AUTH  | Lọc theo giảng viên          |
| GET    | /api/courses?status={status}         | AUTH  | Lọc theo trạng thái          |
| GET    | /api/courses/{course_id}             | AUTH  | Chi tiết khóa học            |
| POST   | /api/courses                         | ADMIN | Tạo khóa học                 |
| PUT    | /api/courses/{course_id}             | ADMIN | Cập nhật khóa học            |
| PUT    | /api/courses/{course_id}/status      | ADMIN | Cập nhật trạng thái khóa học |
| DELETE | /api/courses/{course_id}             | ADMIN | Xóa khóa học                 |

### Lessons

| Method | Endpoint                                 | Quyền          | Mô tả                        |
|--------|------------------------------------------|----------------|------------------------------|
| GET    | /api/courses/{course_id}/lessons         | AUTH           | Danh sách bài học            |
| GET    | /api/lessons/{lesson_id}                 | AUTH           | Chi tiết bài học             |
| GET    | /api/lessons/{lesson_id}/content_preview | AUTH           | Xem trước nội dung bài học   |
| POST   | /api/courses/{course_id}/lessons         | TEACHER, ADMIN | Thêm bài học                 |
| PUT    | /api/lessons/{lesson_id}                 | TEACHER, ADMIN | Cập nhật bài học             |
| PUT    | /api/lessons/{lesson_id}/publish         | TEACHER, ADMIN | Cập nhật trạng thái xuất bản |
| DELETE | /api/lessons/{lesson_id}                 | TEACHER, ADMIN | Xóa bài học                  |

### Enrollments

| Method | Endpoint                                                     | Quyền   | Mô tả                         |
|--------|--------------------------------------------------------------|---------|-------------------------------|
| GET    | /api/enrollments                                             | STUDENT | Danh sách khóa học đã đăng ký |
| POST   | /api/enrollments                                             | STUDENT | Đăng ký khóa học              |
| GET    | /api/enrollments/{enrollment_id}                             | STUDENT | Chi tiết đăng ký/tiến độ      |
| PUT    | /api/enrollments/{enrollment_id}/complete_lesson/{lesson_id} | STUDENT | Đánh dấu hoàn thành bài học   |

### Reviews

| Method | Endpoint                         | Quyền        | Mô tả                       |
|--------|----------------------------------|--------------|-----------------------------|
| GET    | /api/courses/{course_id}/reviews | AUTH         | Danh sách đánh giá khóa học |
| POST   | /api/courses/{course_id}/reviews | STUDENT      | Gửi đánh giá                |
| PUT    | /api/reviews/{review_id}         | OWNER, ADMIN | Cập nhật đánh giá           |
| DELETE | /api/reviews/{review_id}         | OWNER, ADMIN | Xóa đánh giá                |

### Notifications

| Method | Endpoint                                  | Quyền | Mô tả               |
|--------|-------------------------------------------|-------|---------------------|
| GET    | /api/notifications                        | AUTH  | Danh sách thông báo |
| PUT    | /api/notifications/{notification_id}/read | AUTH  | Đánh dấu đã đọc     |
| POST   | /api/notifications                        | ADMIN | Tạo thông báo mới   |
| DELETE | /api/notifications/{notification_id}      | ADMIN | Xóa thông báo       |

### Reports

| Method | Endpoint                                           | Quyền | Mô tả                             |
|--------|----------------------------------------------------|-------|-----------------------------------|
| GET    | /api/reports/top_courses                           | ADMIN | Khóa học phổ biến nhất            |
| GET    | /api/reports/student_progress/{student_id}         | ADMIN | Tiến độ học của một sinh viên     |
| GET    | /api/reports/teacher_courses_overview/{teacher_id} | ADMIN | Tổng quan khóa học của giảng viên |

## Kiểm thử API với Postman

Dự án được kiểm thử bằng Postman. Để import collection:

1. Mở Postman, chọn **Import**
2. Chọn file collection (`.json`) đính kèm trong dự án
3. Tạo Environment với biến `base_url = http://localhost:8080` và `token` để lưu JWT sau khi đăng nhập
4. Gọi `POST /api/auth/login` trước để lấy token, sau đó gán vào biến `token` để dùng cho các request cần xác thực (
   `Authorization: Bearer {{token}}`)