# Course Management System

🇻🇳 [Phiên bản Tiếng Việt](README.md)

An online course management system built with **Spring Boot**, supporting role-based access control (Admin, Teacher,
Student) for managing courses, lessons, enrollments, reviews, and notifications.

## Table of Contents

- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Authorization](#authorization)
- [API Endpoints](#api-endpoints)
- [Testing with Postman](#testing-with-postman)

## Key Features

- User authentication and authorization via JWT (Access Token)
- User management: create, update role, activate/deactivate, delete
- Course management: create, update, change status (DRAFT, PUBLISHED, ARCHIVED), delete
- Lesson management within courses, with visibility control based on publish status
- Course enrollment and lesson-by-lesson progress tracking
- Course reviews and ratings
- System notifications for users
- Reporting: top courses, student progress, teacher course overview
- Course search and filtering by keyword, teacher, or status

## Tech Stack

| Component   | Technology            |
|-------------|-----------------------|
| Language    | Java                  |
| Framework   | Spring Boot           |
| Security    | Spring Security + JWT |
| Data access | Spring Data JPA       |
| Database    | PostgreSQL            |
| Build tool  | Gradle                |
| API testing | Postman               |

## Project Structure

```
src/main/java/com/tuan/course_management
├── config              # Spring Security config, default data initialization
├── controller           # REST controllers
├── dto
│   ├── request           # Request payloads
│   └── response          # Response payloads (including report responses)
├── entity                # JPA entities
├── enums                 # Enums: Role, CourseStatus
├── exception             # Centralized exception handling, error codes
├── mapper                # Entity <-> DTO mapping
├── repository            # Spring Data JPA repositories
├── security              # JWT provider, filter, UserDetails, auth error handling
├── service                # Business logic
└── util                   # Shared utilities (pagination, etc.)

src/main/resources
├── application.properties
├── application-dev.properties
├── application-local.properties
├── application-prod.properties
└── logback-spring.xml
```

## Requirements

- JDK 17 or higher
- PostgreSQL 13 or higher
- Gradle (the bundled Gradle Wrapper works out of the box, no separate install needed)

## Getting Started

1. Clone the repository:

```bash
git clone <repository-url>
cd course_management
```

2. Create a PostgreSQL database:

```sql
CREATE
DATABASE course_management;
```

3. Configure the database connection in `src/main/resources/application-local.properties` (or the profile you use):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/course_management
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>
```

4. Build the project:

```bash
# Windows
.\gradlew.bat build

# Linux/Mac
./gradlew build
```

5. Run the application:

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

By default, the application runs at `http://localhost:8080`.

## Configuration

The project supports multiple profiles:

| Profile | Purpose                           |
|---------|-----------------------------------|
| local   | Local development on your machine |
| dev     | Development environment           |
| prod    | Production environment            |

Select a profile when running:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Authorization

The system uses JWT with the following roles:

| Role    | Description                                        |
|---------|----------------------------------------------------|
| ADMIN   | Full system administration rights                  |
| TEACHER | Manages assigned courses and lessons               |
| STUDENT | Enrolls in courses, learns, and leaves reviews     |
| OWNER   | Resource owner (manages their own profile/reviews) |

Clients must send the token in the header for authenticated requests:

```
Authorization: Bearer <access_token>
```

## API Endpoints

### Auth

| Method | Endpoint         | Access | Description           |
|--------|------------------|--------|-----------------------|
| POST   | /api/auth/login  | PUBLIC | Log in, receive a JWT |
| POST   | /api/auth/verify | AUTH   | Verify a token        |
| GET    | /api/auth/me     | AUTH   | Get current user info |
| POST   | /api/auth/logout | AUTH   | Log out               |

### Users

| Method | Endpoint                      | Access       | Description                 |
|--------|-------------------------------|--------------|-----------------------------|
| GET    | /api/users                    | ADMIN        | List users                  |
| GET    | /api/users?status={status}    | ADMIN        | Filter users by status      |
| GET    | /api/users/{user_id}          | ADMIN        | Get user details            |
| POST   | /api/users                    | ADMIN        | Create a new user           |
| PUT    | /api/users/{user_id}          | OWNER, ADMIN | Update personal info        |
| PUT    | /api/users/{user_id}/role     | ADMIN        | Update user role            |
| PUT    | /api/users/{user_id}/status   | ADMIN        | Activate/deactivate account |
| PUT    | /api/users/{user_id}/password | OWNER, ADMIN | Change password             |
| DELETE | /api/users/{user_id}          | ADMIN        | Delete a user               |

### Courses

| Method | Endpoint                             | Access | Description          |
|--------|--------------------------------------|--------|----------------------|
| GET    | /api/courses                         | AUTH   | List courses         |
| GET    | /api/courses?search={keyword}        | AUTH   | Search by keyword    |
| GET    | /api/courses?teacher_id={teacher_id} | AUTH   | Filter by teacher    |
| GET    | /api/courses?status={status}         | AUTH   | Filter by status     |
| GET    | /api/courses/{course_id}             | AUTH   | Get course details   |
| POST   | /api/courses                         | ADMIN  | Create a course      |
| PUT    | /api/courses/{course_id}             | ADMIN  | Update a course      |
| PUT    | /api/courses/{course_id}/status      | ADMIN  | Update course status |
| DELETE | /api/courses/{course_id}             | ADMIN  | Delete a course      |

### Lessons

| Method | Endpoint                                 | Access         | Description              |
|--------|------------------------------------------|----------------|--------------------------|
| GET    | /api/courses/{course_id}/lessons         | AUTH           | List lessons in a course |
| GET    | /api/lessons/{lesson_id}                 | AUTH           | Get lesson details       |
| GET    | /api/lessons/{lesson_id}/content_preview | AUTH           | Preview lesson content   |
| POST   | /api/courses/{course_id}/lessons         | TEACHER, ADMIN | Add a lesson             |
| PUT    | /api/lessons/{lesson_id}                 | TEACHER, ADMIN | Update a lesson          |
| PUT    | /api/lessons/{lesson_id}/publish         | TEACHER, ADMIN | Update publish status    |
| DELETE | /api/lessons/{lesson_id}                 | TEACHER, ADMIN | Delete a lesson          |

### Enrollments

| Method | Endpoint                                                     | Access  | Description                     |
|--------|--------------------------------------------------------------|---------|---------------------------------|
| GET    | /api/enrollments                                             | STUDENT | List enrolled courses           |
| POST   | /api/enrollments                                             | STUDENT | Enroll in a course              |
| GET    | /api/enrollments/{enrollment_id}                             | STUDENT | Get enrollment/progress details |
| PUT    | /api/enrollments/{enrollment_id}/complete_lesson/{lesson_id} | STUDENT | Mark a lesson as completed      |

### Reviews

| Method | Endpoint                         | Access       | Description         |
|--------|----------------------------------|--------------|---------------------|
| GET    | /api/courses/{course_id}/reviews | AUTH         | List course reviews |
| POST   | /api/courses/{course_id}/reviews | STUDENT      | Submit a review     |
| PUT    | /api/reviews/{review_id}         | OWNER, ADMIN | Update a review     |
| DELETE | /api/reviews/{review_id}         | OWNER, ADMIN | Delete a review     |

### Notifications

| Method | Endpoint                                  | Access | Description                 |
|--------|-------------------------------------------|--------|-----------------------------|
| GET    | /api/notifications                        | AUTH   | List notifications          |
| PUT    | /api/notifications/{notification_id}/read | AUTH   | Mark a notification as read |
| POST   | /api/notifications                        | ADMIN  | Create a new notification   |
| DELETE | /api/notifications/{notification_id}      | ADMIN  | Delete a notification       |

### Reports

| Method | Endpoint                                           | Access | Description                            |
|--------|----------------------------------------------------|--------|----------------------------------------|
| GET    | /api/reports/top_courses                           | ADMIN  | Most popular courses                   |
| GET    | /api/reports/student_progress/{student_id}         | ADMIN  | Progress of a specific student         |
| GET    | /api/reports/teacher_courses_overview/{teacher_id} | ADMIN  | Course overview for a specific teacher |

## Testing with Postman

This project has been tested using Postman. To import the collection:

1. Open Postman and select **Import**
2. Choose the collection file (`.json`) included in this repository
3. Create an Environment with a `base_url = http://localhost:8080` variable and a `token` variable to store the JWT
   after login
4. Call `POST /api/auth/login` first to obtain a token, then set it as the `token` variable to use in authenticated
   requests (`Authorization: Bearer {{token}}`)