package com.example.resilience4j.web;

import com.example.resilience4j.service.CircuitBreakerStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CircuitBreakerStatusController {
    private final CircuitBreakerStatusService status;

    public CircuitBreakerStatusController(CircuitBreakerStatusService status) {
        this.status = status;
    }

    @GetMapping(path = "/circuitbreaker/status")
    public Map<String, Object> status(@RequestParam("circuitBreaker") String circuitBreakerName) {
        return this.status.status(circuitBreakerName);
    }

    @GetMapping(path = "/circuitbreaker/reset")
    public Map<String, Object> reset(@RequestParam("circuitBreaker") String circuitBreakerName) {
        this.status.reset(circuitBreakerName);
        return this.status.status(circuitBreakerName);
    }
}
