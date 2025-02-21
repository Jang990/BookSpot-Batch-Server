package com.bookspot.batch.step.service.memory.book;


public interface InMemoryBookService {
    void add(String isbn13, BookMemoryData data);
    BookMemoryData get(String isbn13);
    boolean contains(String isbn13);
    void clearAll();
}
