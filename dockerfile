FROM maven:3.8.3-jdk-11-slim AS build

WORKDIR /usr/local/src/OT-server
COPY ./ /usr/local/src/OT-server

RUN mvn clean package

COPY ./target/OT-server-0.0.1.jar /usr/local/src/OT-server/target/OT-server-0.0.1.jar
COPY ./target/classes/application.properties /usr/local/src/OT-server/target/classes/application.properties

EXPOSE 8843
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/OT-server-0.0.1.jar"]
