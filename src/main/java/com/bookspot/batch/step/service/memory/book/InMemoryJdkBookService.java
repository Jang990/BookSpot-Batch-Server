package com.bookspot.batch.step.service.memory.book;

import java.util.HashMap;

//@Service
public class InMemoryJdkBookService implements InMemoryBookService {
    private static HashMap<Long, BookMemoryData> store = new HashMap<>();

    public void add(String isbn13, BookMemoryData data) {
        store.put(Long.parseLong(isbn13), data);
    }

    @Override
    public BookMemoryData get(String isbn13) {
        return store.get(Long.parseLong(isbn13));
    }

    public boolean contains(String isbn13) {
        return store.containsKey(Long.parseLong(isbn13));
    }

    public void clearAll() {
        store.clear();
    }
}
