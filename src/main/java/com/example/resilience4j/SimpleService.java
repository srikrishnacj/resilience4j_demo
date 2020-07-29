package com.example.resilience4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.UndeclaredThrowableException;

@Service
public class SimpleService {
    private final SimpleClient simpleClient;
    private static final Logger log = LoggerFactory.getLogger(SimpleService.class);

    public SimpleService(SimpleClient simpleClient) {
        this.simpleClient = simpleClient;
    }

    public void call(boolean shouldFail) {
        try {
            this.simpleClient.call(shouldFail);
        } catch (UndeclaredThrowableException e) {
            log.info("Got Exception: {}", e.getClass().getSimpleName());
            e.printStackTrace();
        } catch (Exception e) {
            log.info("Got Exception: {}", e.getClass().getSimpleName());
        }
    }
}
