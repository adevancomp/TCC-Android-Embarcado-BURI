FROM gradle:8-jdk17 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle
RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew build -x test

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar buri.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "buri.jar"]