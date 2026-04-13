package io.github.bmd007.interview.revoolluut;

import lombok.Builder;
import lombok.With;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Predicate.not;

@With
@Builder
public record LoadBalancable(String serviceName,
                             SelectNextStrategy selectNextStrategy,
                             Map<Integer, String> ips) {
    public static LoadBalancable create(String name, SelectNextStrategy selectNextStrategy) {
        return new LoadBalancable(name, selectNextStrategy, Map.of());
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
        var map = new HashMap<Integer, String>();
        int i = 0;
        for (var ip1 : remaining) {
            map.put(i++, ip1);
        }
        return this.withIps(Map.copyOf(map));
    }

    private LoadBalancable addIp(String ip) {
        if (ips.containsValue(ip)) {
            return this;
        }
        var map = new HashMap<>(ips);
        map.put(map.size(), ip);
        return this.withIps(Map.copyOf(map));
    }

    public Optional<String> nextIp() {
        if (ips.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ips.get(selectNextStrategy.nextIndex(ips.size())));
    }
}
