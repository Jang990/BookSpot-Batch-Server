package com.bookspot.batch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BookDocument {
    private long id;
    private String isbn13;          // isbn
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    private int loanCount;

    @JsonProperty("subject_code")
    private Integer subjectCode; // 주제분류번호
    @JsonProperty("publication_year")
    private Integer publicationYear;

    public BookDocument(
            long id, String isbn13, String title,
            String author, String publisher, int loanCount,
            Integer subjectCode, Integer publicationYear) {
        this.id = id;
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.loanCount = loanCount;
        this.subjectCode = subjectCode;
        this.publicationYear = publicationYear;
    }
}
