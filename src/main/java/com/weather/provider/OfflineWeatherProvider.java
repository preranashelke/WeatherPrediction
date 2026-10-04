package com.weather.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.model.WeatherData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OfflineWeatherProvider {
    private final ObjectMapper objectMapper;

    public OfflineWeatherProvider() { this.objectMapper = new ObjectMapper(); }

    public List<WeatherData> getForecast(String city) {
        try (var input = new ClassPathResource("offlineData.json").getInputStream()) {

            var root = objectMapper.readTree(input);
            List<WeatherData> result = new ArrayList<>();

            for (var item : root) {
                result.add(new WeatherData(LocalDate.parse(item.path("date").asText()),
                        item.path("highTemperature").asDouble(), item.path("lowTemperature").asDouble(),
                        item.path("rain").asBoolean(), item.path("windSpeed").asDouble(),
                        item.path("thunderstorm").asBoolean(), item.path("timeWindow").asText()));
            }
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Bundled forecast data is unavailable", exception);
        }
    }
}
