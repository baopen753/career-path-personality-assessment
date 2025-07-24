FROM openjdk:23-slim

WORKDIR /app

COPY target/eureka-server-1.0.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]