FROM openjdk:8-jdk-alpine

COPY target/backend-1.0-SNAPSHOT.jar backend-1.0-SNAPSHOT.jar
COPY target/libs/ libs/

EXPOSE 8080

ENTRYPOINT ["java", "-DLogback.configurationFile=config/logback.xml", "-jar", "backend-1.0-SNAPSHOT.jar", "-cp", "libs/*" ]
