package com.bookspot.batch;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;

import java.time.Year;

public class BookTestDataBuilder {
    String isbn13 = "1234567890123";
    String title = "임시 제목";
    String author = "임시 저자";
    String publisher = "임시 출판사";
    String volume = "임시 볼륨";
    int loanCount = 0;
    Integer subjectCode = null;
    Year publicationYear = Year.of(2025);

    public ConvertedUniqueBook build() {
        return new ConvertedUniqueBook(
                isbn13, title, author, publisher, volume,
                loanCount, subjectCode, publicationYear
        );
    }

    public BookTestDataBuilder isbn(String isbn13) {
        this.isbn13 = isbn13;
        return this;
    }
}
