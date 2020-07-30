package com.example.resilience4j.web;

import com.example.resilience4j.TypeOfCall;
import com.example.resilience4j.web.to.SimpleWebRequest;

public abstract class BaseController {
    protected void setDefaults(SimpleWebRequest request) {
        if (request.getNoOfCalls() < 1) {
            request.setNoOfCalls(1);
        }

        if (request.getTypeOfCall() == null) {
            request.setTypeOfCall(TypeOfCall.SUCCESS);
        }

        if (request.getResponseDurationMs() < 0) {
            request.setResponseDurationMs(0);
        }

        if (request.getParallelRequests() < 1) {
            request.setParallelRequests(1);
        }
    }
}
