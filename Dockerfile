# ================================
# Stage 1: Build Spring Boot app
# ================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


# ================================
# Stage 2: Run Spring Boot app
# ================================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8081}"]