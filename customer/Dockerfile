FROM maven:3.6.3-jdk-11 as build
WORKDIR /app
COPY pom.xml .
COPY /src ./src
RUN mvn clean package

FROM openjdk:17-slim
COPY --from=build /app/target/*.jar /app.jar
