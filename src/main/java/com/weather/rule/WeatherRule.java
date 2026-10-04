package com.weather.rule;

import com.weather.model.WeatherData;

import java.util.Optional;

public interface WeatherRule {

    Optional<String> evaluate(WeatherData weather);
}