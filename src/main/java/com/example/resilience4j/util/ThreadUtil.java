package com.example.resilience4j.util;

public class ThreadUtil {
    public static void sleep(int durationInMs) {
        try {
            Thread.sleep(durationInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
