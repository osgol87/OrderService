# Use Maven to build the application, then use a minimal JRE image to run it
FROM docker.io/eclipse-temurin:21-jdk-alpine-3.22 AS build
WORKDIR /app

# Copy pom and wrapper to leverage Docker cache
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

FROM docker.io/eclipse-temurin:21-jre-alpine-3.22
WORKDIR /app
# Using a wildcard is more robust to version changes
COPY --from=build /app/target/orderservice-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
