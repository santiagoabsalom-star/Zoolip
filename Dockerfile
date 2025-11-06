FROM maven:4.0.0-rc-4-amazoncorretto-21-debian AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


FROM amazoncorretto:21-alpine3.18
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 3050
EXPOSE 9010
ENTRYPOINT ["java","-XX:+UseZGC","-Xmx16g","-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=9010","-Dcom.sun.management.jmxremote.rmi.port=9010","-Dcom.sun.management.jmxremote.local.only=false","-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false","-Djava.rmi.server.hostname=0.0.0.0", "-XX:+PrintCommandLineFlags", "-jar", "app.jar"]

USER nobody