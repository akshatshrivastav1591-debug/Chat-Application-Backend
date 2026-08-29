FROM eclipse-temurin:25-jdk
LABEL maintainer="Akshat Shrivastav"
WORKDIR /mychatapp
COPY build/libs/ChatApplication-0.0.1-SNAPSHOT.jar chatapp.jar

ENTRYPOINT ["java", "-jar","chatapp.jar"]