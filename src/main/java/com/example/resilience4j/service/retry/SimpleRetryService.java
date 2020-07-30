package com.example.resilience4j.service.retry;

import com.example.resilience4j.client.AbstractSimpleClient;
import com.example.resilience4j.web.to.SimpleWebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.UndeclaredThrowableException;

@Service
public class SimpleRetryService {
    private static final Logger log = LoggerFactory.getLogger(SimpleRetryService.class);
    private final AbstractSimpleClient abstractSimpleClient;

    public SimpleRetryService(AbstractSimpleClient abstractSimpleClient) {
        this.abstractSimpleClient = abstractSimpleClient;
    }

    public void call(SimpleWebRequest request) {
        log.info("From service about to call client");
        try {
            if (request.isShouldUseRetryFallback()) {
                this.abstractSimpleClient.retry_failover(request);
            } else {
                this.abstractSimpleClient.only_retry(request);
            }
        } catch (UndeclaredThrowableException e) {
            // this exception will occure when problem with circuit breaker config.
            // call with this exception are counted for circuit breaker meterics
            // for example no fallback method for given name
            log.info("Client Returned Exception : {}", e.getClass().getSimpleName());
            e.printStackTrace();
        } catch (Exception e) {
            log.info("Client Returned Exception: {}", e.getClass().getSimpleName());
        }
    }
}
