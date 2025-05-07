package com.bookspot.batch.step.service.memory.loan;

import org.eclipse.collections.impl.map.mutable.primitive.LongIntHashMap;
import org.springframework.stereotype.Component;

//@Component
public class SingleMapLoanCountService implements LoanCountService {
    private LongIntHashMap map;

    @Override
    public void init() {
        map = new LongIntHashMap(BOOK_TABLE_SIZE);
    }

    @Override
    public void add(String isbn13) {
        map.put(Long.parseLong(isbn13), 0);
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
        map.forEachKeyValue(consumer::accept);
    }

    @Override
    public void increase(String isbn, int loanCount) {
        if(loanCount == 0)
            return;
        if(!contains(isbn))
            throw new IllegalArgumentException("찾을 수 없는 ISBN");

        long key = Long.parseLong(isbn);
        map.put(key, map.get(key) + loanCount);
    }
}
