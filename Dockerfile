FROM openjdk:17-alpine
EXPOSE 8080
USER root
WORKDIR /usr/src/java-app
COPY build/libs/*.jar ./opt/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]