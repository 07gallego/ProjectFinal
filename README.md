# UniRemington - Sistema Académico Distribuido

Sistema de gestión académica distribuido basado en microservicios con Spring Boot y Spring Cloud.

## Arquitectura

| Servicio | Puerto | Descripción |
|---|---|---|
| eureka-server | 8761 | Servidor de registro y descubrimiento |
| api-gateway | 8080 | Enrutador central de peticiones |
| ms-courses | 8081 | Gestión de cursos y control de cupos |
| ms-students | 8082 | Gestión de estudiantes y matrículas |

## Stack Tecnológico
- Java 21
- Spring Boot 3.2.5
- Spring Cloud (Eureka, Gateway)
- Spring Data JPA + H2
- JUnit 5 + Mockito
- Jacoco (cobertura mínima 80%)
- Swagger/OpenAPI

## Cómo ejecutar
1. Iniciar `eureka-server`
2. Iniciar `api-gateway`
3. Iniciar `ms-courses`
4. Iniciar `ms-students`

## Testing
- ms-courses: 25 tests (12 controller + 13 service)
- ms-students: 23 tests (10 controller + 13 service)