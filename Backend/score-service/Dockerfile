FROM openjdk:23
LABEL authors="Wierzba"

WORKDIR /app

COPY target/score-service-0.0.1-SNAPSHOT.jar score-service.jar

CMD ["java", "-jar", "score-service.jar"]