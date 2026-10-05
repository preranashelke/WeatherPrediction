package com.weather.provider;

import com.weather.model.WeatherData;
import java.util.List;


public interface WeatherProvider {
    List<WeatherData> getForecast(String city, boolean offline, int days);
}
