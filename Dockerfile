FROM openjdk:11-jre-slim

COPY /build/libs/project-matching.jar project-matching.jar

CMD ["sh", "-c", "java -Dspring.profiles.active=prod ${JAVA_OPTS} -jar /project-matching.jar"]