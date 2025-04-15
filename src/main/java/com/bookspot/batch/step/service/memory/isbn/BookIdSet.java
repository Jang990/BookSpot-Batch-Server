package com.bookspot.batch.step.service.memory.isbn;

import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;


public class BookIdSet {
    private final LongHashSet set = new LongHashSet();

    public void add(long bookId) {
        set.add(bookId);
    }

    public boolean contains(long bookId) {
        return set.contains(bookId);
    }
}
