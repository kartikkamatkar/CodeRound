# Multi-stage Dockerfile for CodeRound Spring Boot Backend

# Stage 1: Build application using Maven and Temurin JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first to take advantage of Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Download dependencies offline (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy project source code
COPY src ./src

# Build JAR package skipping tests (tests executed during CI/CD)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root group and user for security
RUN addgroup -S coderound && adduser -S coderound -G coderound

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown -R coderound:coderound /app

USER coderound:coderound

# Expose Spring Boot default port
EXPOSE 8080

# Configure JVM memory limits and run the app
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
