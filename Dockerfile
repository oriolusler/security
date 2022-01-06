FROM openjdk:17-alpine
WORKDIR /opt
COPY build/libs/*.jar /opt/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar