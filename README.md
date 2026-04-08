# Badminton System

Badminton Reservation Management System - Spring Boot Edition

## Tech Stack

- Java 17
- Spring Boot 3.4.5
- Spring Data JPA + Hibernate
- Thymeleaf (Phase 1)
- MS SQL Server
- Lombok
- Maven

## Project Structure (Package by Feature)

`
com.badminton/
├── BadmintonApplication.java
├── config/          ... WebConfig, CORS, etc.
├── common/          ... Interceptor, ExceptionHandler
├── member/          ... Entity, Repository, Service, Controller
├── admin/           ... Entity, Repository, Service, Controller
├── venue/           ... Entity, Repository, Service, Controller
├── court/           ... Entity, Repository, Service, Controller
├── booking/         ... Entity, Repository, Service, Controller
├── timeslot/        ... Entity, Repository, Service
├── product/         ... Entity, Repository, Service, Controller
├── order/           ... Entity, Repository, Service, Controller
├── announcement/    ... Entity, Repository, Service, Controller
└── pickupgame/      ... Entity, Repository, Service, Controller
`

## Getting Started

1. Clone this repository
2. Update pplication.yml with your SQL Server credentials
3. Run: mvn spring-boot:run
4. Open: http://localhost:8080

## Module Assignments

| Module | Owner | Status |
|---|---|---|
| member | TBD | Not started |
| admin | TBD | Not started |
| venue | TBD | Not started |
| court | TBD | Not started |
| booking | TBD | Not started |
| timeslot | TBD | Not started |
| product | TBD | Not started |
| order | TBD | Not started |
| announcement | TBD | Not started |
| pickupgame | TBD | Not started |
