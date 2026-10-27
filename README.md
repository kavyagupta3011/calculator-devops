# calculator-devops

A **Calculator REST API** built with **Java 21 + Spring Boot + Maven**, wrapped in an end-to-end **CI/CD pipeline**:

```
GitHub ──webhook──▶ Jenkins ──▶ Maven build & test ──▶ Docker image ──▶ Docker Hub ──▶ Docker Compose deploy
```

> 🚧 **Work in progress.** This README grows with the project; full build, run and API documentation will be added as each part is completed.

## Course

CSE 816 – Software Production Engineering · Mini-Project (Term I, 2026-27) · IIIT Bangalore

## Planned API

| Operation | Endpoint | Example |
|---|---|---|
| Add | `GET /api/v1/add?a=&b=` | `a + b` |
| Subtract | `GET /api/v1/subtract?a=&b=` | `a - b` |
| Multiply | `GET /api/v1/multiply?a=&b=` | `a * b` |
| Divide | `GET /api/v1/divide?a=&b=` | `a / b` (`b = 0` → HTTP 400) |
| Health | `GET /actuator/health` | `UP` / `DOWN` |

## Tech stack

| Area | Tool |
|---|---|
| Language / framework | Java 21, Spring Boot 3, Maven |
| Testing | JUnit 5, Spring MockMvc |
| Version control | Git, GitHub (feature branches + pull requests) |
| CI/CD | Jenkins (declarative `Jenkinsfile`, GitHub webhook) |
| Containers | Docker (multi-stage build), Docker Hub |
| Deployment | Docker Compose (API + Nginx reverse proxy) |

## Author

Kavya Gupta · [@kavyagupta3011](https://github.com/kavyagupta3011)
