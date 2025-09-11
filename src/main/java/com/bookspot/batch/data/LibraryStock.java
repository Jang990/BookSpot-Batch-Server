package com.bookspot.batch.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class LibraryStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long libraryId;
    private Long bookId;
    private String subjectCode;

    @CreatedDate
    private LocalDate createdAt;
    @Column(name = "updated_at_time")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public LibraryStock(Long libraryId, Long bookId, String subjectCode) {
        this.libraryId = libraryId;
        this.bookId = bookId;
        this.subjectCode = subjectCode;
    }
}
