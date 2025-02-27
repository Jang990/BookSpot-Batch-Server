package com.bookspot.batch.step.service.memory.bookid;

import java.util.HashMap;

//@Service
public class IsbnJdkMemoryRepository implements IsbnMemoryRepository {
    private static HashMap<Long, Long> store = new HashMap<>();

    public void add(Isbn13MemoryData data) {
        store.put(Long.parseLong(data.isbn13()), data.bookId());
    }

    @Override
    public Long get(String isbn13) {
        return store.get(Long.parseLong(isbn13));
    }

    public boolean contains(String isbn13) {
        return store.containsKey(Long.parseLong(isbn13));
    }

    public void clearMemory() {
        store.clear();
    }
}
