# Book Store API

A RESTful backend for an online book store, built with **Spring Boot**. The project is developed incrementally through a pull-request workflow with mentor code reviews, focusing on clean layered architecture, security, and production-style practices.

> **Status:** in active development. The core domain, JWT security, search, validation and API documentation are implemented and covered by CI. Ordering/cart features and containerization are on the roadmap.

## Features

- **JWT authentication & authorization** with Spring Security — stateless sessions, BCrypt password hashing, and role-based access (`ROLE_USER`, `ROLE_ADMIN`) enforced via `@PreAuthorize`.
- **User registration & login** — `POST /api/auth/registration` and `POST /api/auth/login` (returns a JWT).
- **Book catalog** — full CRUD with pagination, plus filtered search (by title, author, ISBN) using JPA Specifications.
- **Categories** — CRUD with a many-to-many relationship to books, and listing books by category.
- **Soft delete** — records are flagged rather than removed, via Hibernate `@SQLDelete` and `@Where`.
- **DTO mapping** with MapStruct to keep entities and API contracts separated.
- **Bean Validation** on requests (`@NotBlank`, `@Email`, `@Size`, `@Positive`) plus a custom `@FieldMatch` validator for password confirmation.
- **Global exception handling** — `CustomGlobalExceptionHandler` returns structured JSON (timestamp, status, path, errors) for `EntityNotFoundException`, `RegistrationException`, `BadCredentialsException`, and validation errors.
- **Database migrations** managed by Liquibase (sequential changesets).
- **Interactive API docs** via Swagger UI (springdoc-openapi).
- **Continuous Integration** — GitHub Actions builds and tests (`mvn verify`) against a MySQL service on every push and pull request.

## Tech Stack

| Area | Technology |
|------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 (Web, Data JPA, Security) |
| Auth | Spring Security + JWT (jjwt 0.11.5) |
| Persistence | MySQL 8, Hibernate/JPA, Liquibase |
| Mapping | MapStruct |
| Validation | Bean Validation (+ custom `@FieldMatch`) |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Quality | Checkstyle |
| CI | GitHub Actions (build + tests on MySQL) |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+ (or use the bundled `./mvnw` wrapper)
- A running MySQL 8 instance

### Configuration

The application reads the following environment variables:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/book_store
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET=your_secret_key
JWT_EXPIRATION=3600000
```

### Run

```bash
git clone https://github.com/AliakseiS97/book-store-spring-boot.git
cd book-store-spring-boot

# Liquibase applies migrations on startup
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

### API Documentation

Once running, open Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

## Authentication Flow

1. Register: `POST /api/auth/registration`.
2. Log in: `POST /api/auth/login` → receive a JWT.
3. Send the token on protected endpoints: `Authorization: Bearer <token>`.

A `JwtAuthenticationFilter` validates the token on every request; `JwtUtil` handles generation, parsing and validation.

## API Endpoints

### Authentication — `/api/auth`
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/registration` | Public | Register a new user |
| POST | `/login` | Public | Authenticate, returns a JWT |

### Books — `/api/books`
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/` | USER, ADMIN | List all books (paginated) |
| GET | `/{id}` | USER, ADMIN | Get a book by id |
| GET | `/search` | USER, ADMIN | Search by title / author / ISBN (paginated) |
| POST | `/` | ADMIN | Create a book |
| PUT | `/{id}` | ADMIN | Update a book |
| DELETE | `/{id}` | ADMIN | Soft-delete a book |

### Categories — `/api/categories`
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/` | USER, ADMIN | List categories |
| GET | `/{id}` | USER, ADMIN | Get a category |
| GET | `/{id}/books` | USER, ADMIN | List books in a category |
| POST | `/` | ADMIN | Create a category |
| PUT | `/{id}` | ADMIN | Update a category |
| DELETE | `/{id}` | ADMIN | Soft-delete a category |

## Domain Model

- **Book** — title, author, ISBN (unique), price, description, cover image, soft-delete flag; many-to-many with Category.
- **Category** — name, description; soft-delete flag.
- **User** — email (unique), password, first/last name, shipping address; many-to-many with Role.
- **Role** — `ROLE_USER` / `ROLE_ADMIN`, implements `GrantedAuthority`.

## Project Structure

```
src/main/java/...
├── config          # Spring Security & application configuration
├── controller      # REST controllers
├── dto             # request / response DTOs
├── exception       # CustomGlobalExceptionHandler & custom exceptions
├── mapper          # MapStruct mappers
├── model           # JPA entities
├── repository      # Spring Data JPA repositories (+ Specifications)
├── security        # JwtUtil, JwtAuthenticationFilter, auth services
├── service         # business logic
└── validation      # custom validators (@FieldMatch)
src/main/resources
└── db/changelog    # Liquibase migrations
```

## Roadmap

- [ ] Shopping cart & orders (Order, OrderItem, ShoppingCart, CartItem)
- [ ] Dockerfile & docker-compose for one-command setup
- [ ] Integration tests with Testcontainers
- [ ] Expanded unit test coverage

## License

Built for learning and portfolio purposes.
