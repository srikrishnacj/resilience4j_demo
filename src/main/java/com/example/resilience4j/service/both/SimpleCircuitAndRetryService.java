package com.example.resilience4j.service.both;

import com.example.resilience4j.TypeOfCall;
import com.example.resilience4j.client.AbstractSimpleClient;
import com.example.resilience4j.web.to.SimpleWebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.UndeclaredThrowableException;

@Service
public class SimpleCircuitAndRetryService {
    private final AbstractSimpleClient abstractSimpleClient;
    private static final Logger log = LoggerFactory.getLogger(SimpleCircuitAndRetryService.class);

    public SimpleCircuitAndRetryService(AbstractSimpleClient abstractSimpleClient) {
        this.abstractSimpleClient = abstractSimpleClient;
    }

    public void call(SimpleWebRequest request) {
        log.info("Entering SimpleCircuitAndRetryService.call()");
        boolean shouldFail = TypeOfCall.FAIL == request.getTypeOfCall() ? true : false;
        int responseDurationMs = request.getResponseDurationMs();
        boolean shouldUseCircuitFallback = request.isShouldUseCircuitFallback();
        boolean shouldUseRetryFallback = request.isShouldUseRetryFallback();

        try {
            if (shouldUseCircuitFallback && shouldUseRetryFallback) {
                this.abstractSimpleClient.circuit_failover_and_retry_failover(request);
            } else if (shouldUseCircuitFallback && !shouldUseRetryFallback) {
                this.abstractSimpleClient.circuit_failover_and_retry(request);
            } else if (!shouldUseCircuitFallback && shouldUseRetryFallback) {
                this.abstractSimpleClient.circuit_and_retry_failover(request);
            } else if (!shouldUseCircuitFallback && !shouldUseRetryFallback) {
                this.abstractSimpleClient.circuit_and_retry(request);
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
            log.info("Leaving SimpleCircuitAndRetryService.call()");
        }
    }
}
