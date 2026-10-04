package com.weather.rule;

import com.weather.model.WeatherData;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThunderstormRule implements WeatherRule {

    @Override
    public Optional<String> evaluate(WeatherData weather) {

        if (weather.isThunderstorm()) {
            return Optional.of("Don't step out! A Storm is brewing!");
        }

        return Optional.empty();
    }
}
