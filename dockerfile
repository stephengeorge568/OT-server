FROM maven:3.8.3-jdk-11-slim AS build

WORKDIR /usr/local/src/OT-server
COPY ./ /usr/local/src/OT-server

RUN mvn clean package

EXPOSE 8080
ENTRYPOINT ["java","-jar","/OT-server-0.0.1.jar.jar"]
