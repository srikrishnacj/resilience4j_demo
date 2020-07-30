package com.example.resilience4j.service;

import com.example.resilience4j.client.SimpleCircuitBreakerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.UndeclaredThrowableException;

@Service
public class SimpleCircuitBreakerService {
    private final SimpleCircuitBreakerClient simpleClient;
    private static final Logger log = LoggerFactory.getLogger(SimpleCircuitBreakerService.class);

    public SimpleCircuitBreakerService(SimpleCircuitBreakerClient simpleClient) {
        this.simpleClient = simpleClient;
    }

    public void call(boolean shouldFail, int responseDurationMs, boolean shouldUseFallback) {
        log.info("Entering SimpleCircuitBreakerService.call() " + "shouldFail = " + shouldFail + ", responseDurationMs = " + responseDurationMs + ", shouldUseFallback = " + shouldUseFallback);
        try {
            if (shouldUseFallback) {
                this.simpleClient.callWithFallback(shouldFail, responseDurationMs);
            } else {
                this.simpleClient.callWithOutFallback(shouldFail, responseDurationMs);
            }
        } catch (UndeclaredThrowableException e) {
            // this exception will occure when problem with circuit breaker config.
            // call with this exception are counted for circuit breaker meterics
            // for example no fallback method for given name
            log.info("Client Returned Exception : {}", e.getClass().getSimpleName());
            e.printStackTrace();
        } catch (Exception e) {
            log.info("Client Returned Exception: {}", e.getClass().getSimpleName());
        } finally {
            log.info("Leaving SimpleCircuitBreakerService.call()");
        }
    }
}
