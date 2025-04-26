package com.bookspot.batch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TEMP_BookDocument {
    @JsonProperty("book_id")
    private String bookId;
    private String isbn13;          // isbn
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    private int loanCount;

    @JsonProperty("subject_code")
    private Integer subjectCode; // 주제분류번호
    @JsonProperty("publication_year")
    private Integer publicationYear;

    @JsonProperty("library_ids")
    private List<String> libraryIdsArrayString;

    public TEMP_BookDocument(
            String bookId, String isbn13, String title,
            String author, String publisher, int loanCount,
            Integer subjectCode, Integer publicationYear,
            List<String> libraryIds) {
        this.bookId = bookId;
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.loanCount = loanCount;
        this.subjectCode = subjectCode;
        this.publicationYear = publicationYear;
        libraryIdsArrayString = libraryIds;
    }
}
