# calculator-devops

Calculator REST API built with Java 21, Spring Boot and Maven, deployed through a CI/CD pipeline using GitHub, Jenkins, Docker and Docker Compose.

```
 GitHub (webhook) -> Jenkins -> Maven build and test -> Docker image -> Docker Hub -> Docker Compose (Nginx + API)
```

## Tech stack

- Java 21, Spring Boot 3.5, Maven
- JUnit 5, MockMvc
- Git, GitHub
- Jenkins
- Docker, Docker Hub
- Docker Compose, Nginx

## Prerequisites

- JDK 21
- Maven 3.6.3 or newer

## Build and test

```bash
mvn clean package        # compile, run all tests, build target/calculator.jar
mvn test                 # run tests only
```

Test reports (JUnit XML) are written to `target/surefire-reports/`.

## Run locally

```bash
java -jar target/calculator.jar
```

The API listens on port **8081** by default. Set `SERVER_PORT` to use a different port:

```bash
SERVER_PORT=9090 java -jar target/calculator.jar
```

## Run with Docker

The image is published on Docker Hub: [kavyag3011/calculator-devops](https://hub.docker.com/r/kavyag3011/calculator-devops)

```bash
docker run -d --name calculator -p 8081:8081 kavyag3011/calculator-devops:dev
curl "http://localhost:8081/api/v1/add?a=2&b=3"
```

Build the image locally:

```bash
docker build -t kavyag3011/calculator-devops:dev .
```

The image runs as a non-root user and includes a health check on `/actuator/health`; `docker ps` shows the container as `healthy` once the application has started.

## Deploy with Docker Compose

The stack runs the API behind an Nginx reverse proxy:

```
client --:8000--> nginx --(backend network)--> calculator:8081
```

```bash
docker compose up -d          # start (builds the image if it is missing)
docker compose ps             # both services should show (healthy)
curl "http://localhost:8000/api/v1/add?a=2&b=3"
docker compose logs -f        # follow logs
docker compose down           # stop and remove containers and networks
```

| Service | Image | Notes |
|---|---|---|
| `calculator` | `kavyag3011/calculator-devops` | Not published to the host; reachable only through Nginx. Health check on `/actuator/health`, `restart: unless-stopped`. |
| `nginx` | `nginx:1.27-alpine` | Published on port 8000. Forwards `/api/` and `/actuator/health`; everything else returns 404. Starts after the API is healthy. |

Configuration is read from environment variables (defaults in brackets). Copy `.env.example` to `.env` to override them:

| Variable | Purpose |
|---|---|
| `IMAGE_NAME` | Calculator image name (`kavyag3011/calculator-devops`) |
| `IMAGE_TAG` | Calculator image tag (`dev`) |
| `NGINX_PORT` | Host port for Nginx (`8000`) |

## API

All endpoints take two numeric query parameters, `a` and `b`, and return JSON.

| Operation | Endpoint | Example |
|---|---|---|
| Add | `GET /api/v1/add?a=&b=` | `/api/v1/add?a=2&b=3` |
| Subtract | `GET /api/v1/subtract?a=&b=` | `/api/v1/subtract?a=10&b=4.5` |
| Multiply | `GET /api/v1/multiply?a=&b=` | `/api/v1/multiply?a=2.5&b=4` |
| Divide | `GET /api/v1/divide?a=&b=` | `/api/v1/divide?a=7&b=2` |
| Health | `GET /actuator/health` | `UP` / `DOWN` |

### Successful response

```bash
curl "http://localhost:8081/api/v1/add?a=2&b=3"
```

```json
{"operation":"add","a":2,"b":3,"result":5}
```

Calculations use `BigDecimal`, so decimal results are exact (`0.1 + 0.2 = 0.3`). Division is rounded to 16 significant digits.

### Error response

Invalid requests return HTTP **400** with a JSON body:

```bash
curl "http://localhost:8081/api/v1/divide?a=1&b=0"
```

```json
{"timestamp":"2026-10-28T09:52:03.078Z","status":400,"error":"Bad Request","message":"Division by zero is not allowed","path":"/api/v1/divide"}
```

| Case | Example | Message |
|---|---|---|
| Division by zero | `divide?a=1&b=0` | `Division by zero is not allowed` |
| Missing parameter | `add?a=5` | `Required parameter 'b' is missing` |
| Not a number | `add?a=abc&b=3` | `Parameter 'a' must be a number, but was 'abc'` |
| NaN / Infinity | `add?a=NaN&b=1` | `Parameter 'a' must be a number, but was 'NaN'` |

### Health

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Overall application health |
| `/actuator/health/liveness` | Process is running |
| `/actuator/health/readiness` | Ready to accept traffic |

## Project structure

```
src/main/java/com/kavyagupta/calculator/
├── CalculatorApplication.java        entry point
├── controller/CalculatorController   REST endpoints
├── service/CalculatorService         arithmetic
├── dto/                              CalculationResponse, ErrorResponse
└── exception/GlobalExceptionHandler  maps errors to HTTP 400
src/test/java/com/kavyagupta/calculator/
├── service/CalculatorServiceTest         unit tests
├── controller/CalculatorControllerTest   MockMvc tests
└── CalculatorApplicationTests            full context + health checks
```

## Author

Kavya Gupta ([@kavyagupta3011](https://github.com/kavyagupta3011))
