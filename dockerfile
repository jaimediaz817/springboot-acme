#FROM openjdk:17-jdk-slim
#COPY target/acme-pedidos-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/pedidos-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]