package com.bookspot.batch.data.document;

import com.bookspot.batch.data.BookCategories;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookCommonFields implements DocumentIdentifiable {
    @JsonProperty("book_id")
    private String bookId;
    private String isbn13;          // isbn
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    @JsonProperty("publication_year")
    private Integer publicationYear;
    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("subject_code")
    private Integer subjectCode; // 주제분류번호
    @JsonProperty("book_categories")
    private BookCategories bookCategories;

    public BookCommonFields(
            String bookId, String isbn13, String title,
            String author, String publisher, Integer publicationYear,
            LocalDate createdAt, Integer subjectCode, BookCategories bookCategories
    ) {
        this.bookId = bookId;
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.subjectCode = subjectCode;
        this.publicationYear = publicationYear;
        if(!bookCategories.equals(BookCategories.EMPTY_CATEGORY))
            this.bookCategories = bookCategories;
        this.createdAt = createdAt.toString();
    }

    @Override
    @JsonIgnore
    public String getDocumentId() {
        return getBookId();
    }
}
