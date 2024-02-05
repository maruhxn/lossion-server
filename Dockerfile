FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/lossion-*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

# Expose the port that the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "-Djasypt.encryptor.password=${JASYPT_PASSWORD}", "app.jar"]