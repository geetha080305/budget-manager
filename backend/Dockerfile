# Stage 1: Build JAR using official open-source Maven + Eclipse Temurin 21
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Lightweight production JRE image (Temurin Alpine)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built jar from builder stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Health check using Spring Actuator
HEALTHCHECK --interval=10s --timeout=5s --start-period=40s --retries=5 \
  CMD wget --quiet --tries=1 -O - http://localhost:8080/actuator/health | grep -q "UP" || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
