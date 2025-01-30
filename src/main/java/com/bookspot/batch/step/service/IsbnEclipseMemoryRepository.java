package com.bookspot.batch.step.service;

import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.springframework.stereotype.Service;

@Service
public class IsbnEclipseMemoryRepository implements IsbnMemoryRepository {
    private static LongHashSet store = new LongHashSet();


    public void add(String isbn13) {
        store.add(Long.parseLong(isbn13));
    }

    public boolean contains(String isbn13) {
        return store.contains(Long.parseLong(isbn13));
    }
}
