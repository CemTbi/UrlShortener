FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk update && \
    apk upgrade openssl libcrypto3 libexpat && \
    rm -rf /var/cache/apk/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 10000

HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:10000/actuator/health | grep UP || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]