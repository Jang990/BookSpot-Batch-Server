package com.bookspot.batch.data.file.csv;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StockCsvData {
//    private int id;                // 줄 번호
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    private String publicationYear; // 발행년도
    private String isbn;            // ISBN
    //    private String setIsbn;         // 세트 ISBN
//    private String additionalCode;  // 부가기호
    private String volume;         // 권 (권 수)
    private String subjectCode;     // 주제분류번호
    private int numberOfBooks;  // 도서권수
    private int loanCount;      // 대출건수
//    private LocalDate registrationDate; // 등록일자


    public StockCsvData(
            String title, String author, String publisher,
            String publicationYear, String isbn, String volume,
            String subjectCode, int numberOfBooks, int loanCount) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.volume = volume;
        this.subjectCode = subjectCode;
        this.numberOfBooks = numberOfBooks;
        this.loanCount = loanCount;
    }
}
