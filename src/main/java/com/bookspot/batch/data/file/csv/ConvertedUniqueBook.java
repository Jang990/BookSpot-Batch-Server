package com.bookspot.batch.data.file.csv;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Year;

@Getter
@AllArgsConstructor
public class ConvertedUniqueBook {
    private String isbn13;          // isbn
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    private String volume;         // 권 (권 수)
    private int numberOfBooks;  // 도서권수
    private int loanCount;
    private Integer subjectCodePrefix; // 주제분류번호
    private Year publicationYear;
}
