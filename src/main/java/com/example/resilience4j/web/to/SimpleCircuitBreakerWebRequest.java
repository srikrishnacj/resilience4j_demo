package com.example.resilience4j.web.to;

import com.example.resilience4j.TypeOfCall;
import lombok.Data;

@Data
public class SimpleCircuitBreakerWebRequest {
    private int noOfCalls;
    private TypeOfCall typeOfCall;
    private int responseDurationMs;
    private boolean shouldUseFallback;
    private int parallelRequests;
}
