package com.bookspot.batch.step.service.memory.isbn;

public interface IsbnSet {
    void add(String isbn13);

    boolean contains(String isbn13);

    void clearAll();

    void init();

    default void beforeProcess() {}

}
