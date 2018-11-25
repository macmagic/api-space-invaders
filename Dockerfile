FROM openjdk:10-jre-slim
COPY ./build/libs/api-space-invaders-1.0.jar /usr/src/app/
WORKDIR /usr/src/app
EXPOSE 8080
CMD ["java", "-jar", "api-space-invaders-1.0.jar"]
