package com.bookspot.batch.data;


import com.bookspot.batch.data.book.Isbn13;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LibraryBook {
    private Isbn13 isbn13;
    private long libraryId;
    private int loanCount;          // 10
    private LocalDate registrationDate; // 2024-11-29
}
