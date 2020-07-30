package com.example.resilience4j.web.retry;

import com.example.resilience4j.service.retry.RetryStatusService;
import com.example.resilience4j.service.retry.SimpleRetryService;
import com.example.resilience4j.util.IdGenerator;
import com.example.resilience4j.web.BaseController;
import com.example.resilience4j.web.to.SimpleWebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SimpleRetryController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SimpleRetryController.class);
    private final SimpleRetryService simpleService;
    private final RetryStatusService statusService;
    private Random rd = new Random();

    public SimpleRetryController(SimpleRetryService simpleService, RetryStatusService statusService) {
        this.simpleService = simpleService;
        this.statusService = statusService;
    }

    @PostMapping(path = "/simpleRetry")
    public Map<String, Object> backendWithOutFallback(@RequestBody SimpleWebRequest request) throws InterruptedException {
        super.setDefaults(request);
        log.info("Queuing Batch Request Calls: {}", request.toString());

        ExecutorService executorService = Executors.newFixedThreadPool(request.getParallelRequests());
        CountDownLatch latch = new CountDownLatch(request.getNoOfCalls());
        for (int ii = 0; ii < request.getNoOfCalls(); ii++) {
            executorService.submit(() -> {
                try (MDC.MDCCloseable _unused = MDC.putCloseable("rid", IdGenerator.id())) {
                    log.info("Starting Request Execution at Controller");
                    simpleService.call(request);
                    log.info("Request Execution Finished at Controller");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        return statusService.status("simple_retry");
    }
}
