FROM openjdk:23
LABEL authors="WierzbaDev"

WORKDIR /app

COPY target/*.jar eureka-server.jar

CMD ["java", "-jar", "eureka-server.jar"]