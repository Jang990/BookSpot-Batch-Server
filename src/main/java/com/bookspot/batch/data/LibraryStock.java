package com.bookspot.batch.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LibraryStock {
    private Long libraryId;
    private Long bookId;
}
