package com.pweb.backend.controllers;

import com.pweb.backend.services.ExchangeService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/exchange")
    @Secured("ROLE_USER")
    public List<ExchangeService.ExchangeResponse> getExchangeRates() {
        return exchangeService.getExchangeRates();
    }


}
