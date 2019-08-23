package server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private final AtomicInteger generator;
    private final Queue<Integer> freeIds;

    public IdGenerator() {
        this.generator = new AtomicInteger();
        this.freeIds = new ConcurrentLinkedQueue<>();
    }

    public int generate() {
        if (freeIds.isEmpty()) {
            return generator.incrementAndGet();
        }

        return freeIds.poll();
    }

    public void free(int Identifier) {
        freeIds.offer(Identifier);
    }
}
