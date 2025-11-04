FROM maven:4.0.0-rc-4-amazoncorretto-21-debian AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean
RUN mvn package -DskipTests -Pproduction

FROM amazoncorretto:21-alpine3.18
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 3050
ENTRYPOINT ["java","-server", "-jar", "app.jar"]
