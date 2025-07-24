FROM openjdk:23-slim

WORKDIR /app

COPY target/career-service-1.0.jar app.jar

EXPOSE 8085

CMD ["java", "-jar", "app.jar"]