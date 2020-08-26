FROM openjdk:8-jdk-alpine

COPY target/temankondangan-ms-backend-0.0.1-SNAPSHOT.jar /app/temankondangan-ms-backend.jar

CMD ["java", "-jar", "/app/temankondangan-ms-backend.jar"]