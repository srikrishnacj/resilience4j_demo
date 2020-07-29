package com.example.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Resilience4jApplication {
    private static final Logger log = LoggerFactory.getLogger(Resilience4jApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(Resilience4jApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(CircuitBreakerRegistry registry) {
        return args -> {
            registry.circuitBreaker("backend").getEventPublisher().onStateTransition(event -> {
                log.info("CIRCUIT STATE TRANSITION: {}: {} -> {}", event.getCircuitBreakerName(), event.getStateTransition().getFromState(), event.getStateTransition().getToState());
            });
        };
    }
}
