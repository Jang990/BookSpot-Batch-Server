package com.bookspot.batch.data.file.csv;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LibraryStockCsvData {
//    private int id;                // 줄 번호
//    private String title;           // 도서명
//    private String author;          // 저자
//    private String publisher;       // 출판사
//    private Integer publicationYear; // 발행년도
    private String isbn;            // ISBN
    //    private String setIsbn;         // 세트 ISBN
//    private String additionalCode;  // 부가기호
//    private String volume;         // 권 (권 수)
    private String subjectCode;     // 주제분류번호
    private int numberOfBooks;  // 도서권수
    private int loanCount;      // 대출건수
//    private LocalDate registrationDate; // 등록일자


    public LibraryStockCsvData(
            String isbn,
            String subjectCode,
            Integer numberOfBooks,
            Integer loanCount) {
        this.isbn = isbn;
        this.subjectCode = subjectCode;
        this.numberOfBooks = numberOfBooks;
        this.loanCount = loanCount;
    }
}
