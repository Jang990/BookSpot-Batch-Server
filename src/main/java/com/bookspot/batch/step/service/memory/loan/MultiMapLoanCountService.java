package com.bookspot.batch.step.service.memory.loan;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//@Component
public class MultiMapLoanCountService implements LoanCountService {
    private Map<Long, AtomicInteger> map;

    @Override
    public void init() {
        map = new HashMap<>();
    }

    @Override
    public void add(String isbn13) {
        map.put(Long.parseLong(isbn13), new AtomicInteger(0));
    }

    @Override
    public boolean contains(String isbn13) {
        try {
            return map.containsKey(Long.parseLong(isbn13));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void clearAll() {
        map = null;
    }

    @Override
    public void processAll(LongIntPrimitiveConsumer consumer) {
        map.forEach((aLong, atomicInteger) -> {
            consumer.accept(aLong, atomicInteger.get());
        });
    }

    @Override
    public void increase(String isbn, int loanCount) {
        if(loanCount == 0)
            return;
        if(!contains(isbn))
            throw new IllegalArgumentException("찾을 수 없는 ISBN");

        long key = Long.parseLong(isbn);
        map.get(key).getAndAdd(loanCount);
    }
}
