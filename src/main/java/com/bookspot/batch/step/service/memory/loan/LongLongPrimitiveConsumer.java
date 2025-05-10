package com.bookspot.batch.step.service.memory.loan;

@FunctionalInterface
public interface LongLongPrimitiveConsumer {
    void accept(long k, long v);
}
