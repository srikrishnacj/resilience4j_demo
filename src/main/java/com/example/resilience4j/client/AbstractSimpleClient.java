package com.example.resilience4j.client;

import com.example.resilience4j.TypeOfCall;
import com.example.resilience4j.exception.CircuitFailoverException;
import com.example.resilience4j.exception.ClientException;
import com.example.resilience4j.exception.RetryFailoverException;
import com.example.resilience4j.util.ThreadUtil;
import com.example.resilience4j.web.to.SimpleWebRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AbstractSimpleClient {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimpleClient.class);

    @CircuitBreaker(name = "simple_circuit_breaker")
    public void only_circuit(SimpleWebRequest request) {
        this.doWork(request);
    }

    @CircuitBreaker(name = "simple_circuit_breaker", fallbackMethod = "circuitFailover")
    public void circuit_failover(SimpleWebRequest request) {
        this.doWork(request);
    }

    @Retry(name = "simple_retry")
    public void only_retry(SimpleWebRequest request) {
        this.doWork(request);
    }

    @Retry(name = "simple_retry", fallbackMethod = "retryFailover")
    public void retry_failover(SimpleWebRequest request) {
        this.doWork(request);
    }

    @CircuitBreaker(name = "simple_circuit_breaker")
    @Retry(name = "simple_retry")
    public void circuit_and_retry(SimpleWebRequest request) {
        this.doWork(request);
    }

    @CircuitBreaker(name = "simple_circuit_breaker", fallbackMethod = "circuitFailover")
    @Retry(name = "simple_retry")
    public void circuit_failover_and_retry(SimpleWebRequest request) {
        this.doWork(request);
    }

    @CircuitBreaker(name = "simple_circuit_breaker")
    @Retry(name = "simple_retry", fallbackMethod = "retryFailover")
    public void circuit_and_retry_failover(SimpleWebRequest request) {
        this.doWork(request);
    }

    @CircuitBreaker(name = "simple_circuit_breaker", fallbackMethod = "circuitFailover")
    @Retry(name = "simple_retry", fallbackMethod = "retryFailover")
    public void circuit_failover_and_retry_failover(SimpleWebRequest request) {
        this.doWork(request);
    }

    private void doWork(SimpleWebRequest request) {
        ThreadUtil.sleep(request.getResponseDurationMs());
        if (request.getTypeOfCall() == TypeOfCall.FAIL) {
            log.info("Client call going to fail");
            throw new ClientException();
        } else {
            log.info("Client call going to success");
        }
    }

    public void retryFailover(SimpleWebRequest request, Exception exception) {
        log.info("RETRY FAILOVER METHOD CALLED");
        if (request.isShouldRetryFallbackThrowException()) {
            log.info("RETRY FAILOVER Throwing Exception");
            throw new RetryFailoverException();
        }
    }

    public void circuitFailover(SimpleWebRequest request, Exception exception) {
        log.info("CIRCUIT BREAKER FAILOVER METHOD CALLED");
        if (request.isShouldCircuitFallbackThrowException()) {
            log.info("CIRCUIT BREAKER FAILOVER Throwing Exception");
            throw new CircuitFailoverException();
        }
    }
}
