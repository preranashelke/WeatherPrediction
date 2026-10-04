package com.weather.model;

import java.time.LocalDate;
import java.util.List;

public class DailyForecast {
    private final LocalDate date;
    private final double highTemperature;
    private final double lowTemperature;
    private final List<String> predictions;
    private final String timeWindow;

    public DailyForecast(LocalDate date, double highTemperature, double lowTemperature,
                         List<String> predictions, String timeWindow) {
        this.date = date;
        this.highTemperature = highTemperature;
        this.lowTemperature = lowTemperature;
        this.predictions = List.copyOf(predictions);
        this.timeWindow = timeWindow;
    }

    public LocalDate getDate() { return date; }
    public double getHighTemperature() { return highTemperature; }
    public double getLowTemperature() { return lowTemperature; }
    public List<String> getPredictions() { return predictions; }
    public String getTimeWindow() { return timeWindow; }
}
