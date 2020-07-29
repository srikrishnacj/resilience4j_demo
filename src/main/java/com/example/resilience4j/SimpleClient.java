package com.example.resilience4j;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleClient {
    private static final Logger log = LoggerFactory.getLogger(SimpleClient.class);

    @CircuitBreaker(name = "backend", fallbackMethod = "failover")
    public void call(boolean shouldFail) {
        if (shouldFail) {
            log.info("going to fail");
            throw new RuntimeException("fail");
        } else {
            log.info("going to success");
        }
    }

    // only public methods will work as failover due to nature of Spring’s AOP framework
    // https://github.com/resilience4j/resilience4j/issues/527
    // failover method should match the exact parameters or source method with exception parameter at the end
    public void failover(boolean shouldFail, Exception exception) {
        log.info("failover method called");
    }
}