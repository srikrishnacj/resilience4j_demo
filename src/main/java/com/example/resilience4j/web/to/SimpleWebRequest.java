package com.example.resilience4j.web.to;

import com.example.resilience4j.TypeOfCall;
import lombok.Data;

@Data
public class SimpleWebRequest {
    private int noOfCalls;
    private TypeOfCall typeOfCall;
    private int responseDurationMs;
    private int parallelRequests;

    private boolean shouldUseCircuitFallback;
    private boolean shouldCircuitFallbackThrowException;
    private boolean shouldUseRetryFallback;
    private boolean shouldRetryFallbackThrowException;

}