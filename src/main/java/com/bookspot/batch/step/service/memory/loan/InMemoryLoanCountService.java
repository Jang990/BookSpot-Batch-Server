package com.bookspot.batch.step.service.memory.loan;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InMemoryLoanCountService {
    private static Map<Long, Integer> store = new HashMap<>();


    public void add(String isbn13, int loanCount) {
        store.put(Long.parseLong(isbn13), loanCount);
    }

    public Integer get(String isbn13) {
        return store.get(Long.parseLong(isbn13));
    }

    public boolean contains(String isbn13) {
        return store.containsKey(Long.parseLong(isbn13));
    }

    public void clearAll() {
        store.clear();
    }

    public Map<Long, Integer> getData() {
        return store;
    }

    public void increase(String isbn, int loanCount) {
        Integer currentLoanCount = get(isbn);
        if(currentLoanCount == null)
            throw new IllegalArgumentException("찾을 수 없는 ISBN");
        add(isbn, currentLoanCount + loanCount);
    }
}
