FROM openjdk:23
LABEL authors="Wierzba"

WORKDIR /app

COPY target/Notification-Service-0.0.1-SNAPSHOT.jar notification-service.jar

CMD ["java", "-jar", "notification-service.jar"]