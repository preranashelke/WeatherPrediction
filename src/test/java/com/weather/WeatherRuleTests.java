package com.weather;

import com.weather.model.WeatherData;
import com.weather.provider.OfflineWeatherProvider;
import com.weather.rule.RainRule;
import com.weather.rule.TemperatureRule;
import com.weather.rule.ThunderstormRule;
import com.weather.rule.WindRule;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class WeatherRuleTests {
    private WeatherData sample(double high, boolean rain, double wind, boolean storm) {
        return new WeatherData(LocalDate.of(2026, 10, 3), high, 16, rain, wind, storm, "09:00 - 12:00");
    }

    @Test void rainRuleSuggestsUmbrellaOnlyWhenRainIsPredicted() {
        assertEquals("Carry umbrella", new RainRule().evaluate(sample(25, true, 4, false)).orElseThrow());
        assertTrue(new RainRule().evaluate(sample(25, false, 4, false)).isEmpty());
    }

    @Test void heatRuleUsesStrictlyAboveFortyThreshold() {
        var rule = new TemperatureRule();
        assertTrue(rule.evaluate(sample(40, false, 4, false)).isEmpty());
        assertEquals("Use sunscreen lotion", rule.evaluate(sample(40.1, false, 4, false)).orElseThrow());
    }

    @Test void windRuleUsesStrictlyAboveTenMphThreshold() {
        var rule = new WindRule();
        assertTrue(rule.evaluate(sample(20, false, 10, false)).isEmpty());
        assertEquals("It's too windy, watch out!", rule.evaluate(sample(20, false, 10.1, false)).orElseThrow());
    }

    @Test void thunderstormRuleReturnsStormAdvice() {
        assertEquals("Don't step out! A Storm is brewing!", new ThunderstormRule().evaluate(sample(20, false, 4, true)).orElseThrow());
    }

    @Test void bundledOfflineForecastContainsThreeDays() {
        var forecast = new OfflineWeatherProvider().getForecast("London");
        assertEquals(3, forecast.size());
        assertTrue(forecast.stream().anyMatch(WeatherData::isRain));
        assertTrue(forecast.stream().anyMatch(day -> day.getHighTemperature() > 40));
    }
}
