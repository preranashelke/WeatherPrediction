package com.weather.provider;

import com.weather.model.WeatherData;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FallbackWeatherProvider implements WeatherProvider {
    private final OpenWeatherProvider onlineProvider;
    private final OfflineWeatherProvider offlineProvider;

    public FallbackWeatherProvider(OpenWeatherProvider onlineProvider, OfflineWeatherProvider offlineProvider) {
        this.onlineProvider = onlineProvider;
        this.offlineProvider = offlineProvider;
    }

    @Override
    public List<WeatherData> getForecast(String city, boolean offline, int days) {
        if (offline) {
            return offlineProvider.getForecast(city, days);
        }

        try {
            return onlineProvider.getForecast(city, days);
        } catch (RuntimeException exception) {
            return offlineProvider.getForecast(city, days);
        }
    }
}
