package com.bookspot.batch.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class Book_ {
    private long dbBookId;
    private String isbn;
    private String image;
    private String title;
    private String subTitle;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private int pageCount;

    public Book_(
            long dbBookId, String isbn, String image,
            String title, String subTitle, String author,
            String publisher, LocalDate publishedDate, int pageCount) {
        this.dbBookId = dbBookId;
        this.isbn = isbn;
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
    }
}
