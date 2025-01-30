package com.bookspot.batch.step.service;

public interface IsbnMemoryRepository {
    void add(String isbn13);

    boolean contains(String isbn13);

    void clearMemory();
}
