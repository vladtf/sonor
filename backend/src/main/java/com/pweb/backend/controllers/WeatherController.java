package com.pweb.backend.controllers;

import com.pweb.backend.services.WeatherService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final Counter apiRequestsCounter;

    public WeatherController(WeatherService weatherService, MeterRegistry meterRegistry) {
        this.weatherService = weatherService;
        this.apiRequestsCounter = meterRegistry.counter("api_requests_total", Tags.of("endpoint", "/api/weather/all"));
    }

    @GetMapping("/all")
    @Secured("ROLE_USER")
    @Operation(summary = "Get all weather forecasts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of weather forecasts"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
            })
    public List<WeatherService.WeatherForecastResponse> getExchangeRates() {
        apiRequestsCounter.increment();
        return weatherService.getWeatherForecast();
    }
}
