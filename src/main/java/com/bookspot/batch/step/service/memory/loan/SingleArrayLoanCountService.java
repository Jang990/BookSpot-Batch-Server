package com.bookspot.batch.step.service.memory.loan;

import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component
public class SingleArrayLoanCountService implements LoanCountService {
    private static final long EMPTY_SPACE = Long.MAX_VALUE;

    private long[] isbnArray;
    private int[] loanArray;
    private int loanIdx ;

    @Override
    public void init() {
        isbnArray = new long[BOOK_TABLE_SIZE];
        loanArray = new int[BOOK_TABLE_SIZE];
        Arrays.fill(isbnArray, EMPTY_SPACE);
        loanIdx = 0;
    }

    @Override
    public void add(String isbn13) {
        isbnArray[loanIdx++] = Long.parseLong(isbn13);
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
    public void beforeCount() {
        Arrays.sort(isbnArray);
    }

    private int findIdx(String isbn13) {
        return Arrays.binarySearch(isbnArray, Long.parseLong(isbn13));
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
            consumer.accept(isbnArray[i], loanArray[i]);
        }
    }

    @Override
    public void increase(String isbn, int loanCount) {
        if(loanCount == 0)
            return;
        if(!contains(isbn))
            throw new IllegalArgumentException("찾을 수 없는 ISBN");
        loanArray[findIdx(isbn)] += loanCount;
    }
}
