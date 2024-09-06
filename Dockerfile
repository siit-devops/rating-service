FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/rating-service-0.0.1-SNAPSHOT.jar /app/
EXPOSE 8083
CMD ["java", "-jar", "rating-service-0.0.1-SNAPSHOT.jar"]