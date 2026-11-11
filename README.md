# Airline Reservation System

A full-stack airline booking web app. Passengers search for flights, pick a seat on seat map, and book it. Admins manage flights and view registered users.

## Key features
**Passenger**
- Register, log in and log out
- Search flights by origin, destination, and departure date range
- Browse matching flights and choose a seat on the seat map

**Admin**
- Add, edit, and delete flights (a flight with booked seats can't be deleted)
- Filter flights by upcoming, past, or all
- View all registered users

**Security**
- Role-based access (`CLIENT` and `ADMIN`): only admins can manage flights and users list
- Password hashed with BCrypt
- Passengers can only view or cancel their own bookings

## Tech stack
Java 17+, Spring Boot 3, Spring Data JPA (Hibernate), MySQL, Spring Security, Thymeleaf, Bootstrap 5, Maven

**Architecture**
- **Layered design**: controller -> service -> repository. Controllers handle HTTP only, services hold the business rules and transactions, and repositories communicate with MySQL
- **Two front ends, one core:** server-rendered Thymeleaf page and a JSON REST API call the same services
- **DTO and form objects**: keep request input separate from the database entities.

## Key design decisions
- **No double-booking:** optimistic locking plus a unique constraint on `bookings.seat_id`. If two users book the same seat at the same moment, only one succeeds and the other one received "seat is taken" error.
- **Business rules live in the service layer,** so the web pages and the REST API enforce the same rule (for example, departed flights can't be booked).

### Prerequisites
- JDK 17+, Maven 3.9+, MySQL 8+

### Setup
1. Create the database:
   ```sql
   CREATE DATABASE airline_db;
   ```
2. Set your configuration in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/airline_db
   spring.datasource.username=<your-mysql-user>
   spring.datasource.password=<your-mysql-password>
   spring.jpa.hibernate.ddl-auto=update
   app.admin.username=admin
   app.admin.email=admin@example.com
   app.admin.password=<choose-a-password>
   ```
3. Start the app:
   ```bash
   mvn spring-boot:run
   ```
4. Open http://localhost:8080. Tables are created automatically on first start, and the admin account is created from the `app.admin.*` values. Log in with it to manage flights.
