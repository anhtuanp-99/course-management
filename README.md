**English** | [Tiếng Việt](README.vi.md)

# Course Management

> Backend API for an online course management platform, built with Spring Boot — featuring JWT authentication, course/lesson management, enrollment, reviews, notifications, and reporting.
>
> 📚 This is a **personal / learning project**, built to practice REST API design, layered architecture with Spring Boot, and JWT authentication.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Running](#installation--running)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Technical Notes & Principles Applied](#technical-notes--principles-applied)
- [Testing](#testing)
- [Documentation](#documentation)
- [Notes](#notes)

## Overview

**Course Management** is a backend system for an online course platform, providing REST APIs for authentication (register/login), course and lesson management, learning progress tracking, course reviews, notifications, and statistical reports. The project follows a standard Spring Boot layered architecture (Controller – Service – Repository), with SRS and ERD documentation included under `docs/`.

## Features

- User authentication & authorization with JWT (access + refresh tokens)
- Course management (CRUD)
- Lesson management and student learning progress
- Course enrollment
- Course reviews
- Notifications
- Statistical reports
- Input validation and centralized exception handling

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.5** (Web, Data JPA, Security, Validation)
- **PostgreSQL**
- **JJWT 0.12.6** — JSON Web Token handling
- **Lombok**
- **Gradle** (build tool, via Gradle Wrapper)
- **JUnit 5 / Spring Security Test** — testing

## Project Structure

```
course_management/
├── docs/                          # SRS, ERD, design documents
├── src/main/java/com/tuan/course_management/
│   ├── config/                    # Spring configuration (Security, Beans...)
│   ├── controller/                # REST controllers
│   ├── dto/
│   │   ├── request/                # Request DTOs
│   │   └── response/               # Response DTOs
│   ├── entity/                    # JPA entities
│   ├── enums/                     # Shared enums
│   ├── exception/                 # Custom exceptions & centralized error handling
│   ├── mapper/                    # Entity <-> DTO mappers
│   ├── repository/                # Spring Data JPA repositories
│   ├── security/                  # JWT filter, security config
│   ├── service/                   # Business logic
│   └── util/                      # Utility classes
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
└── src/test/                      # Unit / integration tests
```

## Prerequisites

- JDK 21+
- PostgreSQL 14+ (or newer)
- No need to install Gradle separately — the project uses the Gradle Wrapper (`gradlew`)

## Installation & Running

1. Clone the repository

```bash
git clone https://github.com/anhtuanp-99/course-management.git
cd course-management/course_management
```

2. Create a PostgreSQL database

```sql
CREATE DATABASE course_management_db;
```

3. Configure the DB connection and JWT secret (see [Configuration](#configuration))

4. Run the application (defaults to the `dev` profile)

```bash
./gradlew bootRun
```

The application will run at `http://localhost:8080`

## Configuration

The project uses Spring Profiles (`application-dev.properties`, `application-prod.properties`). Key variables to configure:

| Variable | Description | Example (dev) |
|----------|--------------|----------------|
| `spring.datasource.url` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/course_management_db` |
| `spring.datasource.username` / `password` | DB credentials | — |
| `JWT_SECRET` | Secret key for signing JWTs | set via environment variable |
| `jwt.expiration` | Access token expiration (ms) | `86400000` |
| `jwt.refresh-expiration` | Refresh token expiration (ms) | `604800000` |

> The active profile is currently `dev,local` (`spring.profiles.active`) — update this in `application.properties` for production deployment.

## API Endpoints

All APIs are prefixed with `/api/v1`.

| Group | Base path | Controller |
|-------|-----------|------------|
| Auth | `/api/v1/auth` | `AuthController` |
| Users | `/api/v1/users` | `UserController` |
| Courses | `/api/v1/courses` | `CourseController` |
| Lessons | `/api/v1` | `LessonController` |
| Enrollments | `/api/v1/enrollments` | `EnrollmentController` |
| Reviews | `/api/v1` | `ReviewController` |
| Notifications | `/api/v1/notifications` | `NotificationController` |
| Reports | `/api/v1/reports` | `ReportController` |

> Detailed per-endpoint documentation (methods, request/response bodies) should ideally be added via Swagger/OpenAPI or a Postman collection for easier reference.

## Technical Notes & Principles Applied

### Database Indexing

Entities are indexed on columns frequently used for joins or filtering, to speed up queries:

| Table | Index | Purpose |
|-------|-------|---------|
| `users` | `idx_users_username` | Speeds up login lookups |
| `courses` | `idx_courses_teacher_id` | Speeds up filtering courses by teacher |
| `lessons` | `idx_lessons_course_id` | Speeds up fetching lessons by course |
| `notifications` | `idx_notifications_user_id` | Speeds up fetching notifications by user |

Key business constraints are also enforced at the database level via `@UniqueConstraint` (not just validated in the service layer):

- `enrollments`: `UNIQUE(student_id, course_id)` — a student can enroll in a course only once
- `lesson_progress`: `UNIQUE(enrollment_id, lesson_id)` — one progress record per lesson per enrollment
- `reviews`: `UNIQUE(course_id, student_id)` — a student can review a course only once

### Transaction Management (ACID)

Services use Spring's `@Transactional` to ensure data integrity:

- `@Transactional(readOnly = true)` on read methods (better performance, avoids unnecessary locking)
- `@Transactional` on write/update methods — ensures **Atomicity**: if a multi-step DB write (e.g. enrolling in a course + initializing progress records) fails partway through, everything rolls back instead of leaving data in an inconsistent state

### SOLID — and a deliberate trade-off

The project applies SOLID principles partially:

- **Single Responsibility**: clear separation by layer — Controller (handles requests) / Service (business logic) / Repository (DB queries) / Mapper (Entity ↔ DTO conversion)
- **Dependency Injection**: constructor injection via `@RequiredArgsConstructor` (Lombok) instead of field injection (`@Autowired`), for easier testing and clearer code

**A deliberate gap:** services (`CourseService`, `UserService`, etc.) don't currently have separate interfaces (e.g. `ICourseService` + `CourseServiceImpl`) — meaning the **Dependency Inversion Principle (DIP)** isn't fully applied. This was a considered trade-off for a solo development phase: each service has only one implementation, and adding interfaces at this stage would mainly add boilerplate without real benefit yet. Interfaces may be introduced later if more thorough unit testing (cleaner mocking via interfaces) or multiple implementations of a service become necessary.

## Testing

```bash
./gradlew test
```

## Documentation

The `docs/` folder contains:
- `SRS - Quản lý khóa học.pdf` — Software Requirements Specification
- `ERD.png` — Entity Relationship Diagram
- `backend-architecture.md` — Backend architecture overview
- Detailed functional breakdown (PDF)

## Notes

This is a learning project, built to practice designing and implementing a complete backend system following RESTful architecture.
