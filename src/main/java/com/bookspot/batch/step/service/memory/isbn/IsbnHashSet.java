package com.bookspot.batch.step.service.memory.isbn;

import java.util.HashSet;
import java.util.Set;


public class IsbnHashSet implements IsbnSet {
    private Set<Long> set;

    @Override
    public void init() {
        set = new HashSet<>(4_000_000);
    }

    @Override
    public void add(String isbn13) {
        set.add(toLong(isbn13));
    }

    @Override
    public boolean contains(String isbn13) {
        return set.contains(toLong(isbn13));
    }

    @Override
    public void clearAll() {
        set = null;
    }

    private long toLong(String isbn13) {
        return Long.parseLong(isbn13);
    }
}
