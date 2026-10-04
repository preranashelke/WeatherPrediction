package com.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WeatherResponse {
    private final String city;
    private final String source;
    private final List<DailyForecast> forecast;
    private final Links links;

    public WeatherResponse(String city, String source, List<DailyForecast> forecast, Links links) {
        this.city = city;
        this.source = source;
        this.forecast = List.copyOf(forecast);
        this.links = links;
    }

    public String getCity() { return city; }
    public String getSource() { return source; }
    public List<DailyForecast> getForecast() { return forecast; }

    @JsonProperty("_links")
    public Links getLinks() { return links; }

    public static class Links {
        private final Link self;
        private final Link documentation;

        public Links(Link self, Link documentation) {
            this.self = self;
            this.documentation = documentation;
        }

        public Link getSelf() { return self; }
        public Link getDocumentation() { return documentation; }
    }

    public static class Link {
        private final String href;

        public Link(String href) { this.href = href; }

        public String getHref() { return href; }
    }
}
