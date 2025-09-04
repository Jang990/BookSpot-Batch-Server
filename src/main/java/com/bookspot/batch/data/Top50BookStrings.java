package com.bookspot.batch.data;

public record Top50BookStrings(
        String ranking,
        String title,
        String authors,
        String publisher,
        String publicationYear,
        String isbn13,
        String vol,
        String subjectCode,
        String loanCount
) {
}
