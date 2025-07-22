FROM openjdk:23-slim

WORKDIR /app

COPY target/payment-service-1.0.jar app.jar

EXPOSE 8086

CMD ["java", "-jar", "app.jar"]