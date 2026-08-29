# Stage 1: Build the jar using Gradle
FROM gradle:8-jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Run the jar
FROM eclipse-temurin:25-jdk
LABEL maintainer="Akshat Shrivastav"
WORKDIR /mychatapp
COPY --from=build /app/build/libs/ChatApplication-0.0.1-SNAPSHOT.jar chatapp.jar

ENTRYPOINT ["java", "-jar", "chatapp.jar"]