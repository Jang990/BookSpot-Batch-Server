package com.bookspot.batch.step.service.memory.isbn;

import java.util.Arrays;

public class IsbnArraySet implements IsbnSet {
    private static final int ISBN_ARRAY_TOTAL_SIZE = 4_000_000;

    private long[] isbnArray;
    private int idx;

    public void init() {
        isbnArray = new long[ISBN_ARRAY_TOTAL_SIZE];
        Arrays.fill(isbnArray, Long.MAX_VALUE);
        idx = 0;
    }

    public void add(String isbn13) {
        isbnArray[idx] = toLong(isbn13);
        idx++;
    }

    public boolean contains(String isbn13) {
        return findIdx(isbn13) >= 0;
    }

    private int findIdx(String isbn13) {
        return Arrays.binarySearch(isbnArray, toLong(isbn13));
    }

    public void beforeProcess() {
        Arrays.sort(isbnArray);
    }

    public void clearAll() {
        isbnArray = null;
        idx = 0;
    }

    private long toLong(String isbn13) {
        return Long.parseLong(isbn13);
    }

}
