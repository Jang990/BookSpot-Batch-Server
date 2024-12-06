package com.bookspot.batch.data;


import com.bookspot.batch.data.book.Isbn13;
import com.bookspot.batch.data.library.LibraryCode;

import java.time.LocalDate;

public class LibraryBook {
    private Isbn13 isbn13;
    private LibraryCode libraryCode;
    private int loanCount;          // 10
    private LocalDate registrationDate; // 2024-11-29
}
