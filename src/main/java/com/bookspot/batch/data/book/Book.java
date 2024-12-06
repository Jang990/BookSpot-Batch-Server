package com.bookspot.batch.data.book;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Book {
    private String isbn13;          // 9788954605816
    private int volume;             // 3
    private String title;           // 눈물상자
    private String author;          // 한강 글 ;봄로야 그림
    private String publisher;       // 문학동네
    private int publicationYear;    // 2021
    private int loanCount;          // 10
    private LocalDate registrationDate; // 2024-11-29
    private String subjectCode; // 813.7
}