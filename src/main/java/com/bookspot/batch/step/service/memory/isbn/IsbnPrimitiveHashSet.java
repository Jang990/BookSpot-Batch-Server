package com.bookspot.batch.step.service.memory.isbn;

import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;


public class IsbnPrimitiveHashSet implements IsbnSet {
    private LongHashSet set;

    @Override
    public void init() {
        set = new LongHashSet(4_000_000);
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
