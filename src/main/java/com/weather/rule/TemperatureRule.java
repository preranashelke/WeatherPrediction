package com.weather.rule;

import com.weather.model.WeatherData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TemperatureRule implements WeatherRule {

    @Override
    public Optional<String> evaluate(WeatherData weather) {

        if (weather.getHighTemperature() > 40) {
            return Optional.of("Use sunscreen lotion");
        }

        return Optional.empty();
    }
}
