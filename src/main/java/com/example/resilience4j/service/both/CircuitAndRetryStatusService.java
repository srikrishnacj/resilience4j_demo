package com.example.resilience4j.service.both;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CircuitAndRetryStatusService {
    private final CircuitBreakerRegistry registry;
    private final RetryRegistry retryRegistry;

    public CircuitAndRetryStatusService(CircuitBreakerRegistry registry, RetryRegistry retryRegistry) {
        this.registry = registry;
        this.retryRegistry = retryRegistry;
    }

    public Map<String, Object> status(String name) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(name);
        if (circuitBreaker == null) return null;

        Map<String, Object> cb = new HashMap<>();
        cb.put("state", circuitBreaker.getState());
        cb.put("metrics", circuitBreaker.getMetrics());
        cb.put("config", circuitBreaker.getCircuitBreakerConfig());

        Retry retry = retryRegistry.retry(name);
        if (retry == null) return null;

        Map<String, Object> rm = new HashMap<>();
        rm.put("metrics", retry.getMetrics());
        rm.put("config", retry.getRetryConfig());

        Map<String, Object> map = new HashMap<>();
        map.put("circuit", cb);
        map.put("retry", rm);
        return map;
    }

    public void reset(String name) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(name);
        if (circuitBreaker != null) circuitBreaker.reset();
    }
}
