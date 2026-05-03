# Hệ Thống Xét Tuyển Đại Học

A university admissions management system built with Java 17, Maven, Swing, Hibernate 6, and MySQL 8.

## Features
- Login with role-based access (ADMIN / USER)
- Manage users (NguoiDung), candidates (ThiSinh), majors (Nganh), subject combinations (ToHopMon)
- Manage major-subject links, exam scores, bonus points, applications (NguyenVong), conversion tables
- Full CRUD dialogs for every module
- CSV bulk import for all entities
- Pagination and search for candidate list
- Score statistics and application status filter

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8

### Setup
1. Create the database and tables:
   ```bash
   mysql -u root -p < database/schema.sql
   ```
2. Update DB credentials in `src/main/resources/hibernate.cfg.xml` if needed (default: root/root).
3. Build and run:
   ```bash
   mvn package
   java -jar target/HeThongXetTuyenDaiHoc-1.0-SNAPSHOT.jar
   ```

### Default Login
- Username: `admin` / Password: `admin123` (ADMIN role)
- Username: `user1` / Password: `user123` (USER role)

> **Note for production use:** Replace plaintext password storage with BCrypt hashing, use environment variables for DB credentials, and remove the hardcoded fallback credentials in `LoginFrame.java`.