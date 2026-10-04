FROM eclipse-temurin:17-jre
WORKDIR /app
RUN addgroup --system weather && adduser --system --ingroup weather weather
COPY target/WeatherPrediction-0.0.1-SNAPSHOT.jar app.jar
USER weather
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
