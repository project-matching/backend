FROM openjdk:8-jre-alpine

COPY /build/libs/project-matching.jar project-matching.jar

CMD ["java", "-jar", "/project-matching.jar"]