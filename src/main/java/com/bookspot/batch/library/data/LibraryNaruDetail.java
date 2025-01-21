package com.bookspot.batch.library.data;

import java.util.Objects;

public record LibraryNaruDetail(String name, String address, String naruDetail) {
    public LibraryNaruDetail {
        Objects.requireNonNull(name);
        Objects.requireNonNull(address);
        Objects.requireNonNull(naruDetail);
    }
}
