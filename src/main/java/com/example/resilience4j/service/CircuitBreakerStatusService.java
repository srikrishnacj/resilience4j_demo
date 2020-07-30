package com.example.resilience4j.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CircuitBreakerStatusService {
    private final CircuitBreakerRegistry registry;

    public CircuitBreakerStatusService(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    public Map<String, Object> status(String name) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(name);
        if (circuitBreaker == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("state", circuitBreaker.getState());
        map.put("metrics", circuitBreaker.getMetrics());
        map.put("config", circuitBreaker.getCircuitBreakerConfig());
        return map;
    }

    public void reset(String name) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(name);
        if (circuitBreaker != null) circuitBreaker.reset();
    }
}
