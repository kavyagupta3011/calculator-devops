# Stage 1: build the JAR with Maven
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build

# Download dependencies first so this layer is cached until pom.xml changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Then copy the source and package (tests run in the Jenkins Test stage)
COPY src ./src
RUN mvn -B -q package -DskipTests

# Stage 2: run the JAR on a small JRE image
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.title="calculator" \
      org.opencontainers.image.description="Calculator REST API" \
      org.opencontainers.image.source="https://github.com/kavyagupta3011/calculator-devops"

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app
COPY --from=build --chown=app:app /build/target/calculator.jar app.jar

USER app

ENV SERVER_PORT=8081
EXPOSE 8081

HEALTHCHECK --interval=15s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- "http://localhost:${SERVER_PORT}/actuator/health" || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
