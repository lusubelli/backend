FROM openjdk:8-jdk-alpine

COPY backend-1.0-SNAPSHOT.jar backend-1.0-SNAPSHOT.jar
COPY libs/ libs/
# COPY config/ config/
# COPY ssl/ ssl/

EXPOSE 8080

# "--ssl-keystore", "ssl/localhost.keystore", "--ssl-password", "password"
ENTRYPOINT ["java", "-DLogback.configurationFile=config/logback.xml", "-jar", "backend-1.0-SNAPSHOT.jar", "--config", "config/application.conf", "-cp", "libs/*" ]
