package com.pweb.backend.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {
    public List<WeatherForecastResponse> getWeatherForecast() {
        // return a list of mock weather forecasts
        return List.of(
                new WeatherForecastResponse("2021-01-01", "20", "80", "Sunny"),
                new WeatherForecastResponse("2021-01-02", "22", "85", "Cloudy"),
                new WeatherForecastResponse("2021-01-03", "18", "75", "Rainy"),
                new WeatherForecastResponse("2021-01-04", "15", "70", "Rainy"),
                new WeatherForecastResponse("2021-01-05", "16", "72", "Sunny"),
                new WeatherForecastResponse("2021-01-06", "17", "73", "Sunny"),
                new WeatherForecastResponse("2021-01-07", "19", "78", "Sunny"),
                new WeatherForecastResponse("2021-01-08", "21", "82", "Rainy"),
                new WeatherForecastResponse("2021-01-09", "23", "87", "Sunny")
        );
    }

    public static class WeatherForecastResponse {
        public String date;
        public String temperature;
        public String humidity;
        public String condition;

        public WeatherForecastResponse(String date, String temperature, String humidity, String condition) {
            this.date = date;
            this.temperature = temperature;
            this.humidity = humidity;
            this.condition = condition;
        }
    }
}
