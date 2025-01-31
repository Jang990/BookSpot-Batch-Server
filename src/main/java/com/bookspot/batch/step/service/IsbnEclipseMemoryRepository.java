package com.bookspot.batch.step.service;

import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.springframework.stereotype.Service;

@Service
public class IsbnEclipseMemoryRepository implements IsbnMemoryRepository {
    private static LongLongHashMap store = new LongLongHashMap();

    public void add(Isbn13MemoryData data) {
        store.put(Long.parseLong(data.isbn13()), data.bookId());
    }

    @Override
    public Long get(String isbn13) {
        if(contains(isbn13))
            return store.get(Long.parseLong(isbn13));
        return null;
    }

    public boolean contains(String isbn13) {
        return store.containsKey(Long.parseLong(isbn13));
    }

    public void clearMemory() {
        store.clear();
    }

}
