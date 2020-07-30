package com.example.resilience4j.client;

import com.example.resilience4j.util.ThreadUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleCircuitBreakerClient {
    private static final Logger log = LoggerFactory.getLogger(SimpleCircuitBreakerClient.class);

    @CircuitBreaker(name = "backend")
    public void callWithOutFallback(boolean shouldFail, int responseDurationMs) {
        log.info("Entering SimpleCircuitBreakerService.callWithOutFallback() " + "shouldFail = " + shouldFail + ", responseDurationMs = " + responseDurationMs);
        ThreadUtil.sleep(responseDurationMs);
        try {
            if (shouldFail) {
                log.info("Going to fail");
                throw new RuntimeException("fail");
            } else {
                log.info("Going to success");
            }
        } finally {
            log.info("Leaving SimpleCircuitBreakerService.callWithOutFallback()");
        }
    }

    @CircuitBreaker(name = "backend", fallbackMethod = "failover")
    public void callWithFallback(boolean shouldFail, int responseDurationMs) {
        log.info("Entering SimpleCircuitBreakerService.callWithFallback() " + "shouldFail = " + shouldFail + ", responseDurationMs = " + responseDurationMs);
        ThreadUtil.sleep(responseDurationMs);
        try {
            if (shouldFail) {
                log.info("Going to fail");
                throw new RuntimeException("fail");
            } else {
                log.info("Going to success");
            }
        } finally {
            log.info("Leaving SimpleCircuitBreakerService.callWithFallback()");
        }

    }

    // only public methods will work as failover due to nature of Spring’s AOP framework
    // https://github.com/resilience4j/resilience4j/issues/527
    // failover method should match the exact parameters or source method with exception parameter at the end
    public void failover(boolean shouldFail, int responseDurationMs, Exception exception) {
        log.info("Failover method called()");
    }
}