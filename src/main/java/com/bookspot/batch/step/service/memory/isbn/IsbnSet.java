package com.bookspot.batch.step.service.memory.isbn;

import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;


public class IsbnSet {
    private LongHashSet set = new LongHashSet();

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
