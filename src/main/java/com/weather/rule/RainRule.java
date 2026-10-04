package com.weather.rule;

import com.weather.model.WeatherData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RainRule implements WeatherRule {

    @Override
    public Optional<String> evaluate(WeatherData weather) {

        if (weather.isRain()) {
            return Optional.of("Carry umbrella");
        }

        return Optional.empty();
    }
}
