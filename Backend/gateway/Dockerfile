FROM openjdk:23
LABEL authors="WierzbaDev"

WORKDIR /app

COPY target/*.jar gateway.jar

CMD ["java", "-jar", "gateway.jar"]