package com.example.resilience4j.web.circuitbreaker;

import com.example.resilience4j.TypeOfCall;
import com.example.resilience4j.service.circuitbreaker.CircuitBreakerStatusService;
import com.example.resilience4j.service.circuitbreaker.SimpleCircuitBreakerService;
import com.example.resilience4j.web.BaseController;
import com.example.resilience4j.web.to.SimpleWebRequest;
import com.example.resilience4j.util.IdGenerator;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SimpleCircuitBreakerController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SimpleCircuitBreakerController.class);
    private final SimpleCircuitBreakerService simpleService;
    private final CircuitBreakerStatusService statusService;
    private Random rd = new Random();

    public SimpleCircuitBreakerController(SimpleCircuitBreakerService simpleService, CircuitBreakerStatusService statusService, CircuitBreakerRegistry registry) {
        this.simpleService = simpleService;
        this.statusService = statusService;
    }

    @PostMapping(path = "/simpleCircuitBreaker")
    public Map<String, Object> backendWithOutFallback(@RequestBody SimpleWebRequest request) throws InterruptedException {
        super.setDefaults(request);
        log.info("Queuing Service Call: {}", request.toString());

        ExecutorService executorService = Executors.newFixedThreadPool(request.getParallelRequests());
        CountDownLatch latch = new CountDownLatch(request.getNoOfCalls());
        for (int ii = 0; ii < request.getNoOfCalls(); ii++) {
            executorService.submit(() -> {
                try (MDC.MDCCloseable _unused = MDC.putCloseable("rid", IdGenerator.id())) {
                    log.info("Starting Request Execution at Controller");
                    simpleService.call(request);
                    log.info("Request Execution Finished at Controller");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        return statusService.status("simple_circuit_breaker");
    }
}
