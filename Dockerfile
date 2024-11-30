FROM openjdk:17

COPY build/libs/app.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]