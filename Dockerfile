FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
#COPY .mvn .mvn
RUN mvn -q -DskipTests dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system appgroup \
    && useradd --system --gid appgroup --create-home --home-dir /home/appuser appuser \
    && mkdir -p /app/uploads \
    && chown -R appuser:appgroup /app /home/appuser

COPY --from=build --chown=appuser:appgroup /app/target/football-platfrom-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
