package com.pweb.backend.controllers;

import com.pweb.backend.services.WeatherService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    public List<WeatherService.WeatherForecastResponse> getExchangeRates() {
        return weatherService.getWeatherForecast();
    }


}
