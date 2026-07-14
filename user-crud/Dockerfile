FROM eclipse-temurin:8-jre-alpine

WORKDIR /app

COPY target/user-crud.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
