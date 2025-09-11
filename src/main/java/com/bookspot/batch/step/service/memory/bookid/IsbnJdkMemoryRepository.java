package com.bookspot.batch.step.service.memory.bookid;

import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class IsbnJdkMemoryRepository implements IsbnMemoryRepository {
    private static LongLongHashMap store;

    public void init() {
        store = new LongLongHashMap(3_000_000);
    }

    public void add(Isbn13MemoryData data) {
        if(data.bookId() == 0)
            throw new IllegalArgumentException("MySQL 자동증가 키를 사용하므로 0은 거부됨");
        store.put(Long.parseLong(data.isbn13()), data.bookId());
    }

    @Override
    public Long get(String isbn13) {
        try {
            long isbn13Number = Long.parseLong(isbn13);
            long result = store.get(isbn13Number);
            if(result == 0)
                return null;
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean contains(String isbn13) {
        return store.containsKey(Long.parseLong(isbn13));
    }

    public void clearMemory() {
        store = null;
    }
}
