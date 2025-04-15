package com.bookspot.batch.step.service.memory.isbn;

import java.util.HashSet;

public class BookIdSet {
    private final HashSet<Long> set = new HashSet<>();

    public void add(long bookId) {
        set.add(bookId);
    }

    public boolean contains(long bookId) {
        return set.contains(bookId);
    }
}
