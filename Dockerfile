# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

COPY src/ src/
RUN ./mvnw --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S payment && adduser -S payment -G payment

WORKDIR /app

COPY --from=build --chown=payment:payment /workspace/target/payment-service-*.jar app.jar

USER payment

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=20s --retries=10 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
