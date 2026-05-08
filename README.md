<!-- Path: /home/prrrssshhh/PROJECTS/primetrade.AI/README.md -->
Built this as part of a backend internship assignment for Primetrade.ai.
Took around 2 days to get everything working, especially Spring Security.
Learned a lot about JWT and role-based access during this.

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL 8
- Maven
- Basic frontend (HTML/CSS/JS)

## Prerequisites
- Java 17+
- MySQL 8+
- Maven

## Setup
1. Clone the repo and open it in your IDE.
2. Make sure MySQL is running before starting the app, I kept getting connection refused without it.
3. Create database:
   ```bash
   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS primetrade_db;"
   ```
4. Update DB username/password in `src/main/resources/application.properties` if your local setup is different.
5. Start backend:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
6. Open Swagger UI at `http://localhost:8080/swagger-ui.html`.
7. Open `frontend/index.html` in browser and test login/register/tasks.

## API Endpoints
| Method | URL | Auth | Role | Description |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | No | Public | Register user and return JWT |
| POST | `/api/v1/auth/login` | No | Public | Login and return JWT |
| GET | `/api/v1/tasks` | Yes | USER/ADMIN | USER gets own tasks, ADMIN gets all |
| POST | `/api/v1/tasks` | Yes | USER/ADMIN | Create task for logged-in user |
| PUT | `/api/v1/tasks/{id}` | Yes | Owner/ADMIN | Update task |
| DELETE | `/api/v1/tasks/{id}` | Yes | Owner/ADMIN | Delete task |
| GET | `/api/v1/admin/users` | Yes | ADMIN | List all users |
| DELETE | `/api/v1/admin/users/{id}` | Yes | ADMIN | Delete a user |

## Scalability Thoughts
Since JWT is stateless, you can run multiple instances behind a load balancer like Nginx without any session issues.
Redis would help cache the task list queries to reduce DB hits.
If this gets bigger, the auth and task logic could be split into separate microservices.

Was a solid learning project overall 🙂

Raise an issue if something doesn't run on your machine,
happy to help.
