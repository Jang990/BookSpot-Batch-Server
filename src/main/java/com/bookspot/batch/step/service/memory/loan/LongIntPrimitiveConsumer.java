package com.bookspot.batch.step.service.memory.loan;

@FunctionalInterface
public interface LongIntPrimitiveConsumer {
    void accept(long k, int v);
}
