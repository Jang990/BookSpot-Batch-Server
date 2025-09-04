package com.bookspot.batch.data;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;

import java.time.Year;

public record Top50Book(
        int ranking, String title,
        String authors, String publisher,
        Year publicationYear, String isbn13,
        String volume, Integer classNumber /* 813.26 */,
        int loanCountInPeriod
) {
    public ConvertedUniqueBook toEntity() {
        return new ConvertedUniqueBook(
                isbn13, title, authors,
                publisher, volume,
                loanCountInPeriod,
                classNumber,
                publicationYear
        );
    }
}
