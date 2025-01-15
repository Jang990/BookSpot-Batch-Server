package com.bookspot.batch.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LibraryBook {
    private long bookId;
    private long libraryId;
    private int loanCount;          // 10
    private LocalDate registrationDate; // 2024-11-29
}
