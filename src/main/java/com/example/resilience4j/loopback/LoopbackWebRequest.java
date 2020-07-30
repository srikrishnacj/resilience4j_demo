package com.example.resilience4j.loopback;

import lombok.Data;

@Data
public class LoopbackWebRequest {
    int loopbackRequestId;
    int loopbackResponseDurationMs;
}
