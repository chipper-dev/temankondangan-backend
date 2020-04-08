FROM openjdk:8-jdk-alpine

COPY target/backend-apps-0.0.1-SNAPSHOT.jar /app/chipper-backend.jar

CMD ["java", "-jar", "/app/chipper-backend.jar"]