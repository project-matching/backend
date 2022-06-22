FROM openjdk:11-jre-slim

COPY /build/libs/project-matching.jar project-matching.jar

ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "/project-matching.jar"]