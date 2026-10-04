package com.weather.rule;

import com.weather.model.WeatherData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WindRule implements WeatherRule {

    @Override
    public Optional<String> evaluate(WeatherData weather) {

        if (weather.getWindSpeed() > 10) {
            return Optional.of("It's too windy, watch out!");
        }

        return Optional.empty();
    }
}
