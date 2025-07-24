FROM openjdk:23-slim

WORKDIR /app

COPY target/quiz-service-1.0.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]