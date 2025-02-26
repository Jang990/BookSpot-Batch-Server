package com.bookspot.batch.data.file.csv;

import java.util.Objects;

public record AggregatedBook(String isbn13, int loanCount) {
    public AggregatedBook {
        Objects.requireNonNull(isbn13);
    }
}
