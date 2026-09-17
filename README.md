# SP-1 Movie Repository

## Vision

This project is a Java backend for storing and managing movie data.  
The system fetches Danish movie data from TMDb and stores movies, actors, genres, and directors in a PostgreSQL database.

---

## Links

Source code repository:  
<https://github.com/Eligoon/SP1MovieBackend>

---

# Architecture

## System Overview

This project is built as a layered backend architecture using:

- Main layer for running the application and importing data
- TMDb client for communicating with the TMDb API
- Service layer for business logic and DTO-to-Entity conversion
- DAO layer for database access
- Entity layer for database models
- DTO layer for transferring data from TMDb and between application layers

Technologies used:

- Java 25
- Maven
- JPA
- Hibernate
- PostgreSQL
- Lombok
- Jackson
- JUnit
- Testcontainers
- TMDb API

---

## Architecture Diagram

The main flow of the application is:

```text
                         TMDb API
                            |
                            v
                    +---------------+
                    |  TMDbClient   |
                    +---------------+
                            |
                            v
                          DTOs
                            |
                            v
                    +---------------+
                    | MovieService  |
                    +---------------+
                            |
                            v
                    +---------------+
                    |     DAOs      |
                    +---------------+
                     /      |      \
                    /       |       \
                   v        v        v
                Movie     Actor     Genre
                Entity    Entity    Entity
                   |
                   v
                Director
                 Entity
                     \      |      /
                      \     |     /
                       v    v    v
                    PostgreSQL
                     Database