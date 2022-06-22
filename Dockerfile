FROM openjdk:11-jre-slim

COPY /build/libs/project-matching.jar project-matching.jar

ENTRYPOINT ["java -Dspring.profiles.active=dev ${JAVA_OPTS} -jar /project-matching.jar"]