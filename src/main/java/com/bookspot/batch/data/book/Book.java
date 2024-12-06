package com.bookspot.batch.data.book;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Book {
    private String isbn13;          // 9788954605816
    private String title;           // 눈물상자 (제목 + 권 수)
    private String author;          // 한강 글 ;봄로야 그림
    private String publisher;       // 문학동네
    private int publicationYear;    // 2021
    private String subjectCode; // 813.7
}