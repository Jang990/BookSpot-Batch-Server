package com.bookspot.batch.step.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;

//@Service
public class IsbnJdkMemoryRepository implements IsbnMemoryRepository {
    private static HashSet<Long> store = new HashSet<>();

    public void add(String isbn13) {
        store.add(Long.parseLong(isbn13));
    }

    public boolean contains(String isbn13) {
        return store.contains(Long.parseLong(isbn13));
    }

    public void clearMemory() {
        store.clear();
    }
}
