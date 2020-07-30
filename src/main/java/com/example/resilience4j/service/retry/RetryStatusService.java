package com.example.resilience4j.service.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RetryStatusService {
    private final RetryRegistry retryRegistry;

    public RetryStatusService(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    public Map<String, Object> status(String name) {
        Retry retry = retryRegistry.retry(name);
        if (retry == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("metrics", retry.getMetrics());
        map.put("config", retry.getRetryConfig());
        return map;
    }
}
