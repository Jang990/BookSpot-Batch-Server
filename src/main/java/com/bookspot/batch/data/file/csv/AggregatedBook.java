package com.bookspot.batch.data.file.csv;

import java.util.Objects;

public record AggregatedBook(String isbn13, String subjectCode, int loanCount) {
    public AggregatedBook {
        Objects.requireNonNull(isbn13);
    }
}
