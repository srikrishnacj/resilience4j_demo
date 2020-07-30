package com.example.resilience4j.web.circuitbreaker;

import com.example.resilience4j.service.circuitbreaker.CircuitBreakerStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Object status(@RequestParam("circuitBreaker") String circuitBreakerName) {
        Map result = this.status.status(circuitBreakerName);
        if (result == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return this.status.status(circuitBreakerName);
        }
    }

    @GetMapping(path = "/circuitbreaker/reset")
    public Object reset(@RequestParam("circuitBreaker") String circuitBreakerName) {
        this.status.reset(circuitBreakerName);

        Map result = this.status.status(circuitBreakerName);
        if (result == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return this.status.status(circuitBreakerName);
        }
    }
}
