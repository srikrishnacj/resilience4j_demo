package com.example.resilience4j.web.retry;

import com.example.resilience4j.service.retry.RetryStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SimpleRetryStatusController {
    private final RetryStatusService statusService;

    public SimpleRetryStatusController(RetryStatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping(path = "/retry/status")
    public Object status(@RequestParam("retry") String circuitBreakerName) {
        Map result = this.statusService.status(circuitBreakerName);
        if (result == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return this.statusService.status(circuitBreakerName);
        }
    }
}
