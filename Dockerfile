FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY app_eval/pom.xml pom.xml
COPY app_eval/src src
RUN --mount=type=cache,target=/root/.m2 mvn --batch-mode verify

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system shopwise && useradd --system --gid shopwise shopwise
COPY --from=build /build/target/app-0.0.1-SNAPSHOT.jar app.jar
USER shopwise
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
