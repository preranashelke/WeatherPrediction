package com.weather.model;

import java.time.LocalDate;

public class WeatherData {
    private final LocalDate date;
    private final double highTemperature;
    private final double lowTemperature;
    private final boolean rain;
    private final double windSpeed;
    private final boolean thunderstorm;
    private final String timeWindow;

    public WeatherData(LocalDate date, double highTemperature, double lowTemperature,
                       boolean rain, double windSpeed, boolean thunderstorm, String timeWindow) {
        this.date = date;
        this.highTemperature = highTemperature;
        this.lowTemperature = lowTemperature;
        this.rain = rain;
        this.windSpeed = windSpeed;
        this.thunderstorm = thunderstorm;
        this.timeWindow = timeWindow;
    }

    public LocalDate getDate() { return date; }
    public double getHighTemperature() { return highTemperature; }
    public double getLowTemperature() { return lowTemperature; }
    public boolean isRain() { return rain; }
    public double getWindSpeed() { return windSpeed; }
    public boolean isThunderstorm() { return thunderstorm; }
    public String getTimeWindow() { return timeWindow; }
}
