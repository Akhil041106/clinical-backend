# ⚙️ CarePulse Clinical Management System - Backend

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2-blue.svg)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/Database-MySQL%208.0-blue.svg)](https://www.mysql.com/)

This repository contains the complete server-side Java Spring Boot backend for the CarePulse Clinical Management System.

## 📂 Repository Structure

- **`pom.xml`**: Maven dependencies (Spring Boot Web, Data JPA, Security, Thymeleaf, MySQL Driver).
- **`Dockerfile`**: Multi-stage Docker build for containerized deployments (e.g., Render, Docker Compose).
- **`run.bat`**: 1-click Windows desktop startup script.
- **`settings.xml`**: Custom Maven configuration.
- **`src/main/resources/application.properties`**: Server port (`8080`), MySQL database connection (`clinicaldb`), and JPA settings.
- **`src/main/java/com/example/clinicalmanagement/`**:
  - `ClinicalManagementApplication.java`: Main bootstrap class and initial data seeder (Indian doctors, patients, appointments, lab reports).
  - `config/SecurityConfig.java`: Role-based authentication rules (`ADMIN`, `DOCTOR`, `PATIENT`) & CSRF protection.
  - `entity/`: Database entities (`User`, `Doctor`, `Patient`, `Department`, `Appointment`, `Consultation`, `TestReport`).
  - `repository/Repositories.java`: Spring Data JPA repository interfaces.
  - `service/Services.java`: Business logic and database transaction management.
  - `controller/`: Web controllers (`AuthController`, `AdminController`, `DoctorController`, `PatientController`, `AppointmentController`, `PingController`).
- **`src/main/resources/templates/`**: Thymeleaf templates (`admin.html`, `doctor.html`, `index.html`, `login.html`, `patient.html`, `register.html`).
- **`src/main/resources/static/`**: Static web assets (`style.css`, `script.js`).

## 🚀 Getting Started

### Prerequisites
- **JDK 17 or 21+**
- **Maven 3.8+**
- **MySQL 8.0+**

### Running Locally
```bash
# Using Maven
mvn spring-boot:run

# Or run the Windows batch script
.\run.bat
```

### Docker
```bash
# Build image
docker build -t clinical-backend .

# Run container
docker run -p 8080:8080 -e PORT=8080 clinical-backend
```
