package com.bookspot.batch.step.service.memory.loan;

import io.netty.util.collection.LongObjectHashMap;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MemoryLoanCountService {
    private static LongObjectHashMap store = new LongObjectHashMap();
    private static final int INIT_LOAN_COUNT = 0;


    public void add(String isbn13) {
        store.put(Long.parseLong(isbn13), new AtomicInteger(INIT_LOAN_COUNT));
    }

    private AtomicInteger temp_get(String isbn13) {
        return (AtomicInteger) store.get(Long.parseLong(isbn13));
    }

    public Integer get(String isbn13) {
        return temp_get(isbn13).get();
    }

    public boolean contains(String isbn13) {
        try {
            return store.containsKey(Long.parseLong(isbn13));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void clearAll() {
        store.clear();
    }

    public Map<Long, AtomicInteger> getData() {
        return store;
    }

    public void increase(String isbn, int loanCount) {
        Integer currentLoanCount = get(isbn);
        if(currentLoanCount == null)
            throw new IllegalArgumentException("찾을 수 없는 ISBN");
        temp_get(isbn).addAndGet(loanCount);
    }
}
