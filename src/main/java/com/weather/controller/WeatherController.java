package com.weather.controller;

import com.weather.model.WeatherResponse;
import com.weather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String city,
            @RequestParam(defaultValue = "false") boolean offline,
            @RequestParam(defaultValue = "3") int days) {

        if (city == null || city.isBlank() || city.length() > 100 || days < 1 || days > 5) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(weatherService.getWeather(city.trim(), offline, days));
    }

}
