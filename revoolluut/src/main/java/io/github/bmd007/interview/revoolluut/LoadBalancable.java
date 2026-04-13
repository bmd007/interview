package io.github.bmd007.interview.revoolluut;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.function.Predicate.not;

public record LoadBalancable(String serviceName, Map<Integer, String> ips) {
    public static LoadBalancable create(String name) {
        return new LoadBalancable(name, Map.of());
    }

    private static LoadBalancable fromIps(String serviceName, Collection<String> ips) {
        var map = new HashMap<Integer, String>();
        int i = 0;
        for (var ip : ips) {
            map.put(i++, ip);
        }
        return new LoadBalancable(serviceName, Map.copyOf(map));
    }

    public LoadBalancable applyEvent(LoadBalancerEvent event) {
        return switch (event) {
            case LoadBalancerEvent.IpDiscovered discovered -> addIp(discovered.ip());
            case LoadBalancerEvent.IpWentOffline wentOffline -> removeIp(wentOffline.ip());
        };
    }

    private LoadBalancable removeIp(String ip) {
        if (!ips.containsValue(ip)) {
            return this;
        }
        var remaining = ips.values()
            .stream()
            .filter(not(ip::equals))
            .toList();
        return fromIps(serviceName, remaining);
    }

    private LoadBalancable addIp(String ip) {
        if (ips.containsValue(ip)) {
            return this;
        }
        var map = new HashMap<>(ips);
        map.put(map.size(), ip);
        return new LoadBalancable(serviceName, Map.copyOf(map));
    }

    public Optional<String> nextIp() {
        if (ips.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ips.get());
    }
}
