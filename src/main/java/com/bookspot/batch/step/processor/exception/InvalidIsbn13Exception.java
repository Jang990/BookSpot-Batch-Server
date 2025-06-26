package com.bookspot.batch.step.processor.exception;

public class InvalidIsbn13Exception extends RuntimeException {
    private long libraryId;

    public InvalidIsbn13Exception(long libraryId) {
        this.libraryId = libraryId;
    }

    public long getLibraryId() {
        return libraryId;
    }
}
