package io.github.bmd007.interview.revoolluut;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public sealed interface SelectNextStrategy permits SelectNextStrategy.RandomPerService,
            SelectNextStrategy.RoundRobin,
            SelectNextStrategy.ConsistentHashing {
    int nextIndex(int bound);

    record RandomPerService() implements SelectNextStrategy {
        @Override
        public int nextIndex(int bound) {
            return ThreadLocalRandom.current().nextInt(bound);
        }
    }

    @Data
    @AllArgsConstructor
    non-sealed class RoundRobin implements SelectNextStrategy {
        private int last;

        @Override
        public int nextIndex(int bound) {
            if (last + 1 < bound) {
                last = last + 1;
                return last;
            }
            last = 0;
            return last;
        }
    }

    non-sealed class ConsistentHashing implements SelectNextStrategy {
        private final int virtualNodes;
        private TreeMap<Integer, Integer> ring = new TreeMap<>();

        public ConsistentHashing(int virtualNodes) {
            this.virtualNodes = virtualNodes;
        }

        public void rebuild(Collection<String> ips) {
            ring = new TreeMap<>();
            int index = 0;
            for (var ip : ips) {
                for (int v = 0; v < virtualNodes; v++) {
                    int hash = hash(ip + "#" + v);
                    ring.put(hash, index);
                }
                index++;
            }
        }

        public int nextIndexForKey(String requestKey) {
            if (ring.isEmpty()) {
                return -1;
            }
            int hash = hash(requestKey);
            SortedMap<Integer, Integer> tailMap = ring.tailMap(hash);
            int ringPosition = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
            return ring.get(ringPosition);
        }

        @Override
        public int nextIndex(int bound) {
            return nextIndexForKey(String.valueOf(ThreadLocalRandom.current().nextInt()));
        }

        private static int hash(String key) {
            // FNV-1a inspired hash — simple, good distribution
            int hash = 0x811c9dc5;
            for (int i = 0; i < key.length(); i++) {
                hash ^= key.charAt(i);
                hash *= 0x01000193;
            }
            return hash & Integer.MAX_VALUE;
        }
    }
}
