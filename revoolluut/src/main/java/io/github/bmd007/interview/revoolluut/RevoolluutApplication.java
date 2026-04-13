package io.github.bmd007.interview.revoolluut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class RevoolluutApplication {

    static void main(String[] args) {
        SpringApplication.run(RevoolluutApplication.class, args);
    }


    Map<String, LoadBalancable> loadBalancables = new ConcurrentHashMap<>();
    BlockingQueue<LoadBalancerEvent> events = new LinkedBlockingQueue<>();

    Runnable eventLoop = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            LoadBalancerEvent loadBalancerEvent;
            try {
                loadBalancerEvent = events.take();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            try {
                loadBalancables.compute(loadBalancerEvent.serviceName(),
                    (serviceName, loadBalancable) -> {
                        if (loadBalancable != null) {
                            return loadBalancable.applyEvent(loadBalancerEvent);
                        }
                        return LoadBalancable.create(serviceName, new SelectNextStrategy.RandomPerService())
                            .applyEvent(loadBalancerEvent);
                    }
                );
            } catch (Exception _) {
                //todo log
                events.add(loadBalancerEvent);
            }
        }
    };

    @EventListener(ApplicationReadyEvent.class)
    public void start(ApplicationReadyEvent event) {
        Thread.ofVirtual().name("event-loop").start(eventLoop);
    }

}
