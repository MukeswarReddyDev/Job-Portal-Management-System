# Job Portal Management System

A REST API-based Job Portal Management System built with **Java 17** and **Spring Boot**. The application follows a **layered monolithic architecture** and uses MySQL for persistent data storage.

Repository: https://github.com/MukeswarReddyDev/Job-Portal-Management-System

## Overview

The system provides separate workflows for job seekers, recruiters, and administrators. Job seekers can manage their profiles and apply for jobs; recruiters can manage job postings and applications after approval; administrators can manage platform access and review dashboards.

## Technology Stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| Backend | Spring Boot 4.1.2 |
| Build tool | Maven |
| Database | MySQL |
| Persistence | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT |
| API documentation | Swagger UI / OpenAPI |
| API testing | Postman |

## Architecture

The project uses a **layered monolithic architecture**. The application is deployed as one Spring Boot application, with responsibilities separated into packages.

```text
src/main/java/com/mkr/
├── configuration/   # Application and security configuration
├── controller/      # REST API endpoints
├── dto/             # Request and response objects
├── enums/           # Roles and domain status values
├── exception/       # Custom exceptions and global handling
├── model/           # JPA entities
├── repository/      # Spring Data JPA repositories
├── security/        # JWT and authentication components
├── service/         # Service interfaces
└── serviceImpl/     # Service implementations
```

The exact package contents may evolve as the project is extended.

## Main Modules

### 1. Authentication and User Management
- User registration and profile management.
- Role-based user access.
- Password handling and change-password functionality.
- JWT-based authentication.

### 2. Job Seeker
- Manage job seeker profile and skills.
- View and filter job opportunities.
- Apply for jobs.
- Track application status.
- Access the job seeker dashboard.
- Manage resume information and uploaded resumes.

### 3. Recruiter
- Recruiter registration and administrator approval workflow.
- Manage recruiter profile.
- Create and manage job postings.
- Review applications and update application statuses.
- Access the recruiter dashboard.

### 4. Job Management
- Create and manage job postings.
- Support job details such as job type and work mode.
- Manage job status and job discovery/filtering.

### 5. Administrator
- Initial administrator account configuration.
- Review and approve recruiters.
- Access the administrator dashboard.
- Role-based access control for protected operations.

## Prerequisites

Install the following before running the application:

- JDK 17
- MySQL Server
- Git
- An IDE such as Eclipse or IntelliJ IDEA
- Postman (for API testing)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/MukeswarReddyDev/Job-Portal-Management-System.git
cd Job-Portal-Management-System
```

### 2. Create the MySQL database

Create the database in MySQL:

```sql
CREATE DATABASE job_portal_management;
```

### 3. Configure environment variables

The application reads database credentials and security secrets from environment variables. Set these variables in your IDE's Run Configuration or in your operating system before starting the application.

| Variable | Purpose | Example |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/job_portal_management` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | Set your own local value |
| `JWT_SECRET` | JWT signing secret | Set a strong private secret |
| `ADMIN_EMAIL` | Initial administrator email | `admin@jobportal.com` |
| `ADMIN_NAME` | Initial administrator name | `Job Portal Admin` |
| `ADMIN_PASSWORD` | Initial administrator password | Set your own private value |

**Security:** Never commit real passwords, JWT secrets, private keys, or uploaded resumes to GitHub. Do not copy your local secrets into this README. Configure your own values locally.

### 4. Run the application

Open the project in your IDE and run the main Spring Boot application class:

`com.mkr.JobPoratlApplication`

Alternatively, use the Maven wrapper.

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On macOS/Linux/Git Bash:

```bash
./mvnw spring-boot:run
```

The application uses port **8080** by default unless configured otherwise.

## API Documentation and Testing

- Use **Swagger UI / OpenAPI** to explore the available API endpoints.
- Use **Postman** to send requests and test authentication and role-based access.
- For protected endpoints, authenticate and provide the JWT as a Bearer token where required.

Swagger UI is available at the standard Springdoc Swagger UI path when the application is running:

`http://localhost:8080/swagger-ui/index.html`

## Resume Uploads

The application is configured to store uploaded resumes under:

`uploads/resumes`

The upload directory is excluded from version control to avoid publishing personal documents. Create the directory locally if it is not created automatically by the application.

## Git Ignore and Sensitive Data

The repository excludes generated build output and local/private files such as:

- Maven `target/` output
- Uploaded resume files
- Local environment files
- Private key files

Keep credentials in environment variables or another local secret-management mechanism, not in source control.

## Future Improvements

Possible future enhancements include:

- Automated unit and integration test coverage.
- More detailed API examples and endpoint documentation.
- Deployment configuration and production environment profiles.
- Additional search, filtering, and reporting capabilities.

## Author

**Mukeswar Reddy**

- GitHub: https://github.com/MukeswarReddyDev

---

If you find an issue or have a suggestion, feel free to open an issue in this repository.
