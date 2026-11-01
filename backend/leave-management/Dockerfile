FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd --system --create-home spring
COPY --from=build /workspace/target/*.jar /app/app.jar
RUN chown spring:spring /app/app.jar

USER spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
