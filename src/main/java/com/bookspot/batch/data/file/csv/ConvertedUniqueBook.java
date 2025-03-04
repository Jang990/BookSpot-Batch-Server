package com.bookspot.batch.data.file.csv;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Year;

@Entity
@Table(name="book")
@Getter
public class ConvertedUniqueBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 13)
    private String isbn13;          // isbn
    private String title;           // 도서명
    private String author;          // 저자
    private String publisher;       // 출판사
    private int loanCount;
//    private int numberOfBooks;  // 도서권수
    private Integer subjectCode; // 주제분류번호
    private Year publicationYear;

    public ConvertedUniqueBook(
            String isbn13, String title, String author,
            String publisher, String volume,
            int loanCount, Integer subjectCode,
            Year publicationYear) {
        this.isbn13 = isbn13;
        this.title = createTitle(title, volume);
        this.author = author;
        this.publisher = publisher;
        this.loanCount = loanCount;
        this.subjectCode = subjectCode;
        this.publicationYear = publicationYear;
    }

    private String createTitle(String title, String volume) {
        if(volume == null || volume.isBlank())
            return title;
        return title.concat(" (%s)".formatted(volume));
    }
}
