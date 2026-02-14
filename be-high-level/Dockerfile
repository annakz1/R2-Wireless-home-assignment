FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build/be-high-level
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /opt/be-high-level
COPY --from=build /build/be-high-level/target/be-high-level-1.0.0.jar be-high-level.jar
EXPOSE 4000
ENTRYPOINT ["java", "-jar", "be-high-level.jar"]
