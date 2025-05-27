FROM openjdk:23-slim

WORKDIR /app

COPY target/user-servce-1.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]