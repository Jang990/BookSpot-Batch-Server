package com.bookspot.batch.data.file.csv;

import lombok.Getter;

import java.time.Year;

@Getter
public class ConvertedUniqueBook {
    private String isbn13;          // isbn
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    private String volume;         // 권 (권 수)
    private int numberOfBooks;  // 도서권수
    private int loanCount;
    private Integer subjectCode; // 주제분류번호
    private Year publicationYear;

    public ConvertedUniqueBook(
            String isbn13, String title, String author,
            String publisher, String volume,
            int numberOfBooks, int loanCount, Integer subjectCode,
            Year publicationYear) {
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.volume = volume;
        this.numberOfBooks = numberOfBooks;
        this.loanCount = loanCount;
        this.subjectCode = subjectCode;
        this.publicationYear = publicationYear;
    }
}
