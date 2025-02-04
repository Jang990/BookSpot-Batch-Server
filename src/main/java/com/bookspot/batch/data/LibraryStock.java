package com.bookspot.batch.data;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class LibraryStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long libraryId;
    private Long bookId;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    public LibraryStock(Long libraryId, Long bookId) {
        this.libraryId = libraryId;
        this.bookId = bookId;
    }
}
