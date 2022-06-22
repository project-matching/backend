FROM openjdk:11-jre-slim

COPY /build/libs/project-matching.jar project-matching.jar
# ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "${JAVA_OPTS}", "/project-matching.jar"]

ENTRYPOINT ["tail", "-f"]