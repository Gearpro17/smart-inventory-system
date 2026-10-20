FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY inventory/pom.xml inventory/pom.xml
COPY inventory/.mvn inventory/.mvn
COPY inventory/mvnw inventory/mvnw
COPY inventory/src inventory/src
RUN chmod +x inventory/mvnw && inventory/mvnw -f inventory/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/inventory/target/inventory-0.0.1-SNAPSHOT.jar /app/inventory.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/inventory.jar"]
