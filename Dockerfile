FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean
RUN mvn package -DskipTests -Pproduction

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 3050
ENTRYPOINT ["java","-server", "-jar", "app.jar"]
