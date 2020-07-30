package com.example.resilience4j.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger counter = new AtomicInteger(1);

    public static String id() {
        return String.format("%03d", counter.getAndIncrement());
    }
}
