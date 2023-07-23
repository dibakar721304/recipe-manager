FROM openjdk:17-jdk-slim

WORKDIR /app
COPY ./target/recipe-manager-0.0.1-SNAPSHOT.jar /app


CMD ["java", "-jar", "recipe-manager-0.0.1-SNAPSHOT.jar"]