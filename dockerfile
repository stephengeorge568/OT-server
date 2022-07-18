FROM maven:3.8.3-jdk-11-slim AS build

WORKDIR /usr/local/src/OT-server
COPY ./ /usr/local/src/OT-server

RUN mvn package

COPY ./target/ /usr/local/src/OT-server/target/
#COPY ./target/OT-server-0.0.1.jar /usr/local/src/OT-server/target/OT-server-0.0.1.jar
#COPY ./target/classes/application.properties /usr/local/src/OT-server/target/classes/application.properties
#COPY ./target/classes/application.properties /usr/local/src/OT-server/target/application.properties

EXPOSE 8443
EXPOSE 8080
RUN ls
#CMD ["mvn spring-boot:run"]
ENTRYPOINT ["java","-jar","target/OT-server-0.0.1.war"]
#ENTRYPOINT ["./mvnw", "spring-boot:run","pom.xml"]
