FROM openjdk:23-slim

WORKDIR /app

COPY target/api-gateway-1.0.jar app.jar

EXPOSE 8072

CMD ["java", "-jar", "app.jar"]