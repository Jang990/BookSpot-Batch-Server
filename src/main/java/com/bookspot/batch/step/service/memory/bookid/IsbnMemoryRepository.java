package com.bookspot.batch.step.service.memory.bookid;

public interface IsbnMemoryRepository {
    void add(Isbn13MemoryData data);
    /** NumberFormatException 발생시 null 반환할 것 */
    Long get(String isbn13);
    boolean contains(String isbn13);
    void clearMemory();
}
