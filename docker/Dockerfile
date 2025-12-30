FROM gradle:8-jdk21 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle.properties ./
COPY libs ./libs
COPY src ./src

RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]