package com.bookspot.batch.book.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String isbn13;          // 9788954605816
    private String title;           // 눈물상자 (제목 + 권 수)
    private String author;          // 한강 글 ;봄로야 그림
    private String publisher;       // 문학동네
    private Integer publicationYear;    // 2021
    private String subjectCode; // 813.7
    private String volumeName;

    public Book(String isbn13, String title, String author, Integer publicationYear, String subjectCode, String volumeName) {
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.subjectCode = subjectCode;
        this.volumeName = volumeName;
    }
}