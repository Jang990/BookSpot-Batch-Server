package com.bookspot.batch.data;

import java.time.LocalDate;
import java.util.Objects;

public record LibraryForFileParsing(long libraryId, String libraryCode, String naruDetail, LocalDate stockUpdatedAt) {
    public LibraryForFileParsing {
        Objects.requireNonNull(libraryCode);
        Objects.requireNonNull(naruDetail);
    }
}
