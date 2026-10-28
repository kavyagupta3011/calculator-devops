# calculator-devops

Calculator REST API built with Java 21, Spring Boot and Maven, deployed through a CI/CD pipeline using GitHub, Jenkins, Docker and Docker Compose.

```
GitHub -> Jenkins -> Maven build and test -> Docker image -> Docker Hub -> Docker Compose
```

## API

| Operation | Endpoint |
|---|---|
| Add | `GET /api/v1/add?a=&b=` |
| Subtract | `GET /api/v1/subtract?a=&b=` |
| Multiply | `GET /api/v1/multiply?a=&b=` |
| Divide | `GET /api/v1/divide?a=&b=` |
| Health | `GET /actuator/health` |

## Tech stack

- Java 21, Spring Boot 3, Maven
- JUnit 5, MockMvc
- Git, GitHub
- Jenkins
- Docker, Docker Hub
- Docker Compose, Nginx

## Author

Kavya Gupta ([@kavyagupta3011](https://github.com/kavyagupta3011))
