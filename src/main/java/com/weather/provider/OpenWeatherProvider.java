package com.weather.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class OpenWeatherProvider {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final double METRES_PER_SECOND_TO_MPH = 2.236936;

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/forecast}")
    private String apiUrl;

    @Value("${weather.api.key:}")
    private String apiKey;

    public List<WeatherData> getForecast(String city) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException("Weather API key is not configured");
        }

        try {
            HttpResponse<String> response = httpClient.send(buildRequest(city), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Weather provider returned HTTP " + response.statusCode());
            }

            return parseForecast(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Weather provider request interrupted", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to fetch weather data", exception);
        }
    }

    private HttpRequest buildRequest(String city) {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        String url = apiUrl + "?q=" + encodedCity + "&appid=" + encodedKey + "&cnt=24&units=metric";

        return HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(4))
                .GET()
                .build();
    }

    private List<WeatherData> parseForecast(String responseBody) throws Exception {
        JsonNode forecastItems = objectMapper.readTree(responseBody).path("list");
        if (!forecastItems.isArray()) {
            throw new IllegalStateException("Weather provider response is invalid");
        }

        Map<LocalDate, List<JsonNode>> itemsByDate = groupItemsByDate(forecastItems);
        List<WeatherData> forecast = new ArrayList<>();

        for (Map.Entry<LocalDate, List<JsonNode>> day : itemsByDate.entrySet()) {
            if (forecast.size() == 3) {
                break;
            }
            forecast.add(toWeatherData(day.getKey(), day.getValue()));
        }

        return forecast;
    }

    private Map<LocalDate, List<JsonNode>> groupItemsByDate(JsonNode forecastItems) {
        Map<LocalDate, List<JsonNode>> itemsByDate = new TreeMap<>();

        for (JsonNode item : forecastItems) {
            LocalDateTime time = LocalDateTime.parse(item.path("dt_txt").asText(), DATE_FORMAT);
            LocalDate date = time.toLocalDate();
            itemsByDate.computeIfAbsent(date, unused -> new ArrayList<>()).add(item);
        }

        return itemsByDate;
    }

    private WeatherData toWeatherData(LocalDate date, List<JsonNode> items) {
        double high = Double.NEGATIVE_INFINITY;
        double low = Double.POSITIVE_INFINITY;
        double fastestWindMph = 0;
        boolean rain = false;
        boolean thunderstorm = false;

        for (JsonNode item : items) {
            high = Math.max(high, item.path("main").path("temp_max").asDouble());
            low = Math.min(low, item.path("main").path("temp_min").asDouble());
            fastestWindMph = Math.max(fastestWindMph, item.path("wind").path("speed").asDouble() * METRES_PER_SECOND_TO_MPH);
            rain |= item.path("rain").path("3h").asDouble() > 0;
            thunderstorm |= hasThunderstorm(item.path("weather"));
        }

        return new WeatherData(date, high, low, rain, fastestWindMph, thunderstorm, "00:00 - 23:59");
    }

    private boolean hasThunderstorm(JsonNode conditions) {
        for (JsonNode condition : conditions) {
            int weatherCode = condition.path("id").asInt();
            if (weatherCode >= 200 && weatherCode < 300) {
                return true;
            }
        }
        return false;
    }
}
