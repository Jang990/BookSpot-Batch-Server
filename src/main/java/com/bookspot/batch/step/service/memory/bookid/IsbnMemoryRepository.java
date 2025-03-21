package com.bookspot.batch.step.service.memory.bookid;

public interface IsbnMemoryRepository {
    void add(Isbn13MemoryData data);
    Long get(String isbn13);
    boolean contains(String isbn13);
    void clearMemory();
}
