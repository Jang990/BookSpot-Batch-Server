package com.bookspot.batch.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn13;          // 9788954605816
    private String title;           // 눈물상자 (제목 + 권 수)
    private String author;          // 한강 글 ;봄로야 그림
    private String publisher;       // 문학동네
    private Integer publicationYear;    // 2021
    private String classification; // 813.7
    private String volumeName;

    public Book(String isbn13, String title, String author, Integer publicationYear, String classification, String volumeName) {
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.classification = classification;
        this.volumeName = volumeName;
    }
}