# Petify Backend

Petify Backend is a Spring Boot–based REST API that powers the Petify platform — a pet care and service marketplace connecting pet owners, caregivers, and administrators.  
The backend is responsible for authentication, business logic, data persistence, and API exposure for the frontend and external services.

---

## Overview

The backend provides:

- Secure user authentication and authorization
- Role-based access control (Admin, Client, Owner)
- Management of pets, listings, reviews, and bookings
- Vet clinic onboarding and approval workflows
- Centralized business rules and validations
- RESTful APIs for frontend consumption

The system is designed with scalability, clarity, and real-world domain modeling in mind.

---

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA (Hibernate)**
- **PostgreSQL**
- **Maven**
- **JWT-based authentication**
- **Docker-ready architecture**

---

## Architecture

The project follows a layered architecture:

- **API Layer**  
  REST controllers exposing endpoints and handling HTTP requests/responses

- **Service Layer**  
  Business logic, validations, and transactional operations

- **Persistence Layer**  
  JPA entities and repositories mapped to PostgreSQL

- **Security Layer**  
  Authentication, authorization, JWT handling, and request filtering

- **Domain Model**  
  Clear separation of Users, Roles, Pets, Listings, Reviews, and Clinics

---

## Core Features

### Authentication & Authorization
- Username/password authentication
- JWT token issuance and validation
- Role-based endpoint protection

### Users & Roles
- Unified `User` model with role specialization
- Support for Admins and Clients
- Account status handling (active, blocked)

### Pets & Owners
- Clients can add pets and become owners
- Pet profiles with documents and metadata
- Owner–pet relationships enforced at database level

### Listings
- Pet-related service listings
- Pricing, location, and availability tracking
- Public and private listing visibility

### Reviews
- Authenticated user reviews
- One-to-many user-to-review relationship
- Review authorship enforcement

### Vet Clinics
- Vet clinic application workflow
- Admin approval or rejection process
- Separation between application and active clinic records

---

## Data Persistence

- PostgreSQL as the primary datastore
- Strong referential integrity using foreign keys
- Lazy and eager loading configured per use case
- Transactional service methods for consistency

---

## Security

- Stateless authentication using JWT
- Password hashing with BCrypt
- Protected endpoints based on roles
- CORS configuration for frontend integration

---

## Configuration

The application is configured via environment variables and Spring profiles.  
Sensitive data such as database credentials and secrets are externalized and not committed to version control.

---

## Running the Application

The backend is designed to run:

- Locally (for development and testing)
- In Docker containers
- As part of a multi-service deployment with frontend and database

Spring profiles allow flexible environment switching.

---

## API Design

- RESTful endpoints
- JSON request/response payloads
- Clear separation of public and protected routes
- Predictable HTTP status codes

---

## Project Goals

- Real-world domain modeling for academic and production use
- Clean, maintainable backend structure
- Ready for extension with AI-driven services
- Suitable for cloud deployment and CI/CD pipelines

---

## License

This project is developed for educational and research purposes.  
All rights reserved.
