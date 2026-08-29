# Stage 1: Build the jar using the Gradle Wrapper
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# Stage 2: Run the jar
FROM eclipse-temurin:25-jdk
LABEL maintainer="Akshat Shrivastav"
WORKDIR /mychatapp
COPY --from=build /app/build/libs/ChatApplication-0.0.1-SNAPSHOT.jar chatapp.jar

ENTRYPOINT ["java", "-jar", "chatapp.jar"]