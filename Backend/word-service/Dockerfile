FROM openjdk:23
LABEL authors="Wierzba"

WORKDIR /app

COPY target/word-service-0.0.1-SNAPSHOT.jar word-service.jar

CMD ["java", "-jar", "word-service.jar"]