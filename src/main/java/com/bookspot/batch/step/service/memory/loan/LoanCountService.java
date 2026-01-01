package com.bookspot.batch.step.service.memory.loan;


public interface LoanCountService {
    int BOOK_TABLE_SIZE = 4_000_000;

    void init();

    void add(String isbn13);

    boolean contains(String isbn13);

    default void beforeCount() {}

    void clearAll();

    void processAll(LongIntPrimitiveConsumer consumer);

    void increase(String isbn, int loanCount);
}
