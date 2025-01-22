package com.bookspot.batch.stock.file;

import java.time.LocalDate;
import java.util.Objects;

public record CurrentLibrary(String libraryCode, String naruDetail, LocalDate stockUpdatedAt) {
    public CurrentLibrary {
        Objects.requireNonNull(libraryCode);
        Objects.requireNonNull(naruDetail);
    }
}
