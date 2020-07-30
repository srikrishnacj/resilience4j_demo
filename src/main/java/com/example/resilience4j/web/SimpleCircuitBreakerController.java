package com.example.resilience4j.web;

import com.example.resilience4j.TypeOfCall;
import com.example.resilience4j.service.SimpleCircuitBreakerService;
import com.example.resilience4j.web.to.SimpleCircuitBreakerWebRequest;
import com.example.resilience4j.util.IdGenerator;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SimpleCircuitBreakerController {
    private static final Logger log = LoggerFactory.getLogger(SimpleCircuitBreakerController.class);
    private final SimpleCircuitBreakerService simpleService;
    private final CircuitBreakerRegistry registry;
    private Random rd = new Random();

    public SimpleCircuitBreakerController(SimpleCircuitBreakerService simpleService, CircuitBreakerRegistry registry) {
        this.simpleService = simpleService;
        this.registry = registry;
    }

    @PostMapping(path = "/backend")
    public Map<String, Object> backendWithOutFallback(@RequestBody SimpleCircuitBreakerWebRequest request) throws InterruptedException {
        this.setDefaults(request);
        log.info("Queuing Service Call: {}", request.toString());

        ExecutorService executorService = Executors.newFixedThreadPool(request.getParallelRequests());
        CountDownLatch latch = new CountDownLatch(request.getNoOfCalls());
        for (int ii = 0; ii < request.getNoOfCalls(); ii++) {
            executorService.submit(() -> {
                callService(request);
                latch.countDown();
            });
        }
        latch.await();
        executorService.shutdown();
        return circuitBreakerStatus();
    }

    private void setDefaults(SimpleCircuitBreakerWebRequest request) {
        if (request.getNoOfCalls() < 1) {
            request.setNoOfCalls(1);
        }

        if (request.getTypeOfCall() == null) {
            request.setTypeOfCall(TypeOfCall.SUCCESS);
        }

        if (request.getResponseDurationMs() < 0) {
            request.setResponseDurationMs(0);
        }

        if (request.getParallelRequests() < 1) {
            request.setParallelRequests(1);
        }
    }

    private void callService(SimpleCircuitBreakerWebRequest request) {
        try (MDC.MDCCloseable _unused = MDC.putCloseable("rid", IdGenerator.id())) {
            log.info("Entering SimpleCircuitBreakerController.callService()");
            if (request.getTypeOfCall() == TypeOfCall.SUCCESS) {
                simpleService.call(false, request.getResponseDurationMs(), request.isShouldUseFallback());
            } else if (request.getTypeOfCall() == TypeOfCall.FAIL) {
                simpleService.call(true, request.getResponseDurationMs(), request.isShouldUseFallback());
            } else {
                if (rd.nextBoolean()) {
                    simpleService.call(true, request.getResponseDurationMs(), request.isShouldUseFallback());
                } else {
                    simpleService.call(false, request.getResponseDurationMs(), request.isShouldUseFallback());
                }
            }
            log.info("Leaving SimpleCircuitBreakerController.callService()");
        }
    }

    private Map<String, Object> circuitBreakerStatus() {
        CircuitBreaker circuitBreaker = registry.circuitBreaker("backend");
        Map<String, Object> map = new HashMap<>();
        map.put("state", circuitBreaker.getState());
        map.put("metrics", circuitBreaker.getMetrics());
        map.put("config", circuitBreaker.getCircuitBreakerConfig());
        return map;
    }
}
