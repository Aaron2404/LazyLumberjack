package dev.boostio.lazylumberjack.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class PacketPool<T> {
    private final ConcurrentLinkedQueue<T> pool = new ConcurrentLinkedQueue<>();
    private final Supplier<T> packetSupplier;

    public PacketPool(Supplier<T> packetSupplier) {
        this.packetSupplier = packetSupplier;
    }

    public T acquire() {
        T packet = pool.poll();
        return (packet != null) ? packet : packetSupplier.get();
    }

    public void release(T packet) {
        pool.offer(packet);
    }
}
