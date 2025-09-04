package com.bookspot.batch.data;

public record Top50BookStrings(
        String no,
        String ranking,
        String title,
        String authors,
        String publisher,
        String publicationYear,
        String isbn13,
        String addition_symbol,
        String vol,
        String subjectCode,
        String categoryNames,
        String loanCount
) {
}
