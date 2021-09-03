package org.lql.netty.chatandrpc.protocol;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.compareAndSet(255, 0) ? id.get() : id.incrementAndGet();
    }
}
