package io.github.bmd007.interview.revoolluut;

public sealed interface LoadBalancerEvent permits LoadBalancerEvent.IpDiscovered, LoadBalancerEvent.IpWentOffline {
    String serviceName();

    record IpDiscovered(String ip, String serviceName) implements LoadBalancerEvent {
    }

    record IpWentOffline(String ip, String serviceName) implements LoadBalancerEvent {
    }
}
