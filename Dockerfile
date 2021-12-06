FROM gradle:7.3-jdk17-alpine

#RUN apk add --update gradle
COPY . /project
RUN  cd /project && gradle build -x test

#run the spring boot application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dblabla", "-jar","/project/build/libs/security-0.0.1-SNAPSHOT.jar"]