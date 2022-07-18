FROM maven:3.8.3-jdk-11-slim AS build

WORKDIR /usr/local/src/OT-server
COPY ./ /usr/local/src/OT-server

RUN mvn package

COPY ./target/ /usr/local/src/OT-server/target/

EXPOSE 8443
ENTRYPOINT ["java","-jar","target/OT-server-0.0.1.war","--spring.profiles.active=prod"]
