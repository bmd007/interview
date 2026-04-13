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
        while (true) {
            try {
                var e = events.take();
                loadBalancables.compute(e.serviceName(),
                    (serviceName, loadBalancable) -> {
                        if (loadBalancable != null) {
                            return loadBalancable.applyEvent(e);
                        }
                        return LoadBalancable.create(serviceName)
                            .applyEvent(e);
                    }
                );
            } catch (Exception _) {
            }
        }
    };

    @EventListener(ApplicationReadyEvent.class)
    public void start(ApplicationReadyEvent event) {
        Thread.ofVirtual().name("event-loop").start(eventLoop);
    }

}
