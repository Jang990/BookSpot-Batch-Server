package com.bookspot.batch.step.service.memory.loan;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Component
public class MultiArrayLoanCountService implements LoanCountService {
    private long[] isbnArray;
    private AtomicIntegerArray loanArray;
    private int loanIdx = 0;

    @Override
    public void init() {
        isbnArray = new long[BOOK_TABLE_SIZE];
        loanArray = new AtomicIntegerArray(BOOK_TABLE_SIZE);
        Arrays.fill(isbnArray, EMPTY_SPACE);
        loanIdx = 0;
    }

    @Override
    public void add(String isbn13) {
        isbnArray[loanIdx++] = Long.parseLong(isbn13);
    }

    @Override
    public void beforeCount() {
        Arrays.sort(isbnArray);
    }

    private int findIdx(String isbn13) {
        return Arrays.binarySearch(isbnArray, Long.parseLong(isbn13));
    }

    @Override
    public boolean contains(String isbn13) {
        try {
            return findIdx(isbn13) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void clearAll() {
        isbnArray = null;
        loanArray = null;
        loanIdx = 0;
    }

    @Override
    public void processAll(LongIntPrimitiveConsumer consumer) {
        for (int i = 0; i < loanIdx; i++) {
            consumer.accept(isbnArray[i], loanArray.get(i));
        }
    }

    @Override
    public void increase(String isbn, int loanCount) {
        if(loanCount == 0)
            return;
        if(!contains(isbn))
            throw new IllegalArgumentException("찾을 수 없는 ISBN");
        loanArray.getAndAdd(findIdx(isbn), loanCount);
    }
}
