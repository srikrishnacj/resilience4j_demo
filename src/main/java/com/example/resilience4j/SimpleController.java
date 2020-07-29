package com.example.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class SimpleController {
    private static final Logger log = LoggerFactory.getLogger(SimpleController.class);
    private final SimpleService simpleService;
    private final CircuitBreakerRegistry registry;
    private Random rd = new Random();

    public SimpleController(SimpleService simpleService, CircuitBreakerRegistry registry) {
        this.simpleService = simpleService;
        this.registry = registry;
    }

    @GetMapping(path = "/backend")
    public Map<String, Object> call(@RequestParam(value = "noOfCalls", required = false) int noOfCalls,
                                    @RequestParam(value = "typeOfCall", required = false) TypeOfCall typeOfCall) {
        if (noOfCalls < 1) {
            noOfCalls = 1;
        }

        if (typeOfCall == null) {
            typeOfCall = TypeOfCall.SUCCESS;
        }

        log.info("Making backend call: noOfCalls: {} typeOfCall: {}", noOfCalls, typeOfCall);

        while (noOfCalls-- != 0) {
            if (typeOfCall == TypeOfCall.SUCCESS) {
                simpleService.call(false);
            } else if (typeOfCall == TypeOfCall.FAIL) {
                simpleService.call(true);
            } else {
                if (rd.nextBoolean()) {
                    simpleService.call(false);
                } else {
                    simpleService.call(true);
                }
            }
        }
        return circuitBreakerStatus();
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
