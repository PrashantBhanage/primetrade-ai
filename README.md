# Primetrade API

Built this as part of a backend internship assignment for Primetrade.ai.
Took around 2 days to get everything working, especially the Spring Security + JWT part.
Learned a lot about role-based access and token-based auth while building this.

---

## What it does

A REST API with user authentication and role-based access control. Users can register, log in, and manage their tasks. Admins can see and manage everything. There's also a basic frontend to interact with the APIs without Postman.

---

## Tech Stack

- **Java 17** + **Spring Boot 3.2.0**
- **Spring Security 6** — JWT-based stateless auth
- **MySQL** + **Spring Data JPA** + **Hibernate**
- **jjwt 0.11.5** — JWT generation and validation
- **BCryptPasswordEncoder** — password hashing
- **Springdoc OpenAPI** — Swagger UI for API docs
- **Lombok** — reduces boilerplate
- **Maven** — build tool
- **Vanilla JS + HTML/CSS** — simple frontend

---

## Project Structure

```
src/main/java/com/primetrade/api/
├── config/         → Security and Swagger configuration
├── controller/     → Auth, Task, Admin controllers
├── dto/            → Request and response objects
├── exception/      → Global error handling
├── model/          → User and Task entities
├── repository/     → JPA repositories
├── security/       → JWT filter, util, UserDetailsService
└── service/        → Business logic

frontend/
└── index.html      → Single page UI (HTML + CSS + JS)
```

---

## Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.8+

---

## Setup & Run

**1. Clone the repo**
```bash
git clone https://github.com/PrashantBhanage/primetrade-ai.git
cd primetrade-ai
```

**2. Create the database**
```bash
mysql -u root -p
```
```sql
CREATE DATABASE primetrade_db;
exit;
```

**3. Update database credentials if needed**

Open `src/main/resources/application.properties` and set your MySQL username and password:
```properties
spring.datasource.username=root
spring.datasource.password=yourpassword
```
> Make sure MySQL is running before starting the app — I kept getting connection refused without it.

**4. Run the app**
```bash
mvn spring-boot:run
```

Hibernate will auto-create the `users` and `tasks` tables on first run.

---

## Access

| | URL |
|---|---|
| API Base | `http://localhost:8080/api/v1` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Frontend | Open `frontend/index.html` in your browser |

---

## API Endpoints

| Method | Endpoint | Auth Required | Role | Description |
|--------|----------|:---:|------|-------------|
| POST | `/api/v1/auth/register` | No | Any | Register a new user |
| POST | `/api/v1/auth/login` | No | Any | Login and get JWT token |
| GET | `/api/v1/tasks` | Yes | Any | Get tasks (admin sees all, user sees own) |
| POST | `/api/v1/tasks` | Yes | Any | Create a new task |
| PUT | `/api/v1/tasks/{id}` | Yes | Owner / Admin | Update a task |
| DELETE | `/api/v1/tasks/{id}` | Yes | Owner / Admin | Delete a task |
| GET | `/api/v1/admin/users` | Yes | Admin only | List all registered users |
| DELETE | `/api/v1/admin/users/{id}` | Yes | Admin only | Delete a user |

---

## How to Test with Swagger

1. Open `http://localhost:8080/swagger-ui.html`
2. Hit `POST /api/v1/auth/register` to create a user
3. Hit `POST /api/v1/auth/login` and copy the token from the response
4. Click the **Authorize** button at the top right
5. Enter `Bearer <your_token>` and click Authorize
6. Now you can test all protected endpoints directly from the browser

---

## Security

- Passwords are hashed using **BCrypt** before storing — plain text is never saved
- JWT tokens are signed with HS256 and expire after **7 days**
- Tokens are validated on every request through a custom `JwtFilter`
- Users can only update or delete their own tasks — admins can access everything
- Input validation runs on all request bodies with proper error messages returned

---

## Scalability Notes

Since JWT is stateless, you can run multiple instances of this app behind a load balancer like Nginx or AWS ALB without any session sharing issues. Each instance just validates the token independently.

A few things that would help at scale:
- **Redis caching** on the task list endpoint to reduce repeated DB hits
- **Database indexing** on `email` in users and `user_id` in tasks — queries get slow without this at volume
- **Pagination** on GET /tasks — not added here but would be needed with real data
- The auth and task logic is already separated enough that they could be split into independent microservices if needed

---

## Database Schema

```sql
users
  id          BIGINT PK AUTO_INCREMENT
  name        VARCHAR NOT NULL
  email       VARCHAR UNIQUE NOT NULL
  password    VARCHAR NOT NULL  -- bcrypt hashed
  role        ENUM('USER', 'ADMIN')
  created_at  DATETIME

tasks
  id          BIGINT PK AUTO_INCREMENT
  title       VARCHAR NOT NULL
  description TEXT
  status      ENUM('TODO', 'IN_PROGRESS', 'DONE')
  user_id     BIGINT FK → users.id
  created_at  DATETIME
```

---

## Sample Request & Response

**Register**
```json
POST /api/v1/auth/register
{
  "name": "Prashant Bhanage",
  "email": "prashant@test.com",
  "password": "123456",
  "role": "ADMIN"
}
```

**Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "prashant@test.com",
  "name": "Prashant Bhanage",
  "role": "ADMIN",
  "userId": 1
}
```

**Create Task**
```json
POST /api/v1/tasks
Authorization: Bearer <token>

{
  "title": "Implement JWT auth",
  "description": "Add login and register with token generation",
  "status": "IN_PROGRESS"
}
```

---

Raise an issue if something doesn't run on your machine, happy to help.
