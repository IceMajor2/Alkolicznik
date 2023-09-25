FROM eclipse-temurin:17
ARG JAR_FILE=/build/libs/alkolicznik-1.0.0.jar
COPY ${JAR_FILE} alkolicznik.jar
ENTRYPOINT ["java", "-jar", "alkolicznik.jar"]