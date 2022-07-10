FROM maven:3.8.3-jdk-11-slim AS build

WORKDIR /usr/local/src/OT-server
COPY ./ /usr/local/src/OT-server

RUN mvn clean package

COPY /usr/local/src/OT-server/target/OT-server-0.0.1.jar /usr/local/src/OT-server/target/OT-server-0.0.1.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/target/OT-server-0.0.1.jar"]
