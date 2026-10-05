package com.weather.service;

import com.weather.model.DailyForecast;
import com.weather.model.WeatherData;
import com.weather.model.WeatherResponse;
import com.weather.provider.WeatherProvider;
import com.weather.rule.WeatherRule;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WeatherService {
    private final WeatherProvider weatherProvider;
    private final List<WeatherRule> rules;

    public WeatherService(WeatherProvider weatherProvider, List<WeatherRule> rules) {
        this.weatherProvider = weatherProvider;
        this.rules = rules;
    }

    public WeatherResponse getWeather(String city, boolean offline, int days) {
        List<WeatherData> data = weatherProvider.getForecast(city, offline, days);

        List<DailyForecast> forecast = data.stream()
                .map(this::buildForecast)
                .toList();

        return new WeatherResponse(city, offline ? "offline" : "live-or-fallback", forecast,
                new WeatherResponse.Links(new WeatherResponse.Link("/api/weather?city=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
                                + "&days=" + days + "&offline=" + offline),
                        new WeatherResponse.Link("/v3/api-docs")));
    }

    private DailyForecast buildForecast(WeatherData weather) {

        List<String> predictions = rules.stream()
                .map(rule -> rule.evaluate(weather))
                .flatMap(Optional::stream)
                .toList();

        return new DailyForecast(weather.getDate(), weather.getHighTemperature(), weather.getLowTemperature(), predictions, weather.getTimeWindow());
    }
}
