package com.bookspot.batch.step.service.memory.bookid;

import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class IsbnJdkMemoryRepository implements IsbnMemoryRepository {
    private static LongLongHashMap store = new LongLongHashMap();

    public void add(Isbn13MemoryData data) {
        store.put(Long.parseLong(data.isbn13()), data.bookId());
    }

    @Override
    public Long get(String isbn13) {
        try {
            long isbn13Number = Long.parseLong(isbn13);
            return store.get(isbn13Number);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean contains(String isbn13) {
        return store.containsKey(Long.parseLong(isbn13));
    }

    public void clearMemory() {
        store.clear();
    }
}
