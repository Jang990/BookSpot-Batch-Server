package com.bookspot.batch.step.service.memory.loan;


public interface LoanCountService {
    long EMPTY_SPACE = Long.MAX_VALUE;
    int BOOK_TABLE_SIZE = 3_000_000;

    void init();

    void add(String isbn13);

    boolean contains(String isbn13);

    default void beforeCount() {}

    void clearAll();

    void processAll(LongIntPrimitiveConsumer consumer);

    void increase(String isbn, int loanCount);
}
