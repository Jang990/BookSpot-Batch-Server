package com.bookspot.batch.step.service.memory.isbn;

import java.util.HashSet;

public class IsbnSet {
    private HashSet<Long> set = new HashSet<>();

    public void add(String isbn13) {
        set.add(toLong(isbn13));
    }

    public boolean contains(String isbn13) {
        return set.contains(toLong(isbn13));
    }

    public void clearAll() {
        set.clear();
    }

    private long toLong(String isbn13) {
        return Long.parseLong(isbn13);
    }
}
