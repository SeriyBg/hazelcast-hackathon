FROM adoptopenjdk/maven-openjdk11 AS build
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline
COPY src /app/src
RUN ["mvn", "clean", "package", "-DskipTests"]

FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY --from=build /app/target/cpu-intensive-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]