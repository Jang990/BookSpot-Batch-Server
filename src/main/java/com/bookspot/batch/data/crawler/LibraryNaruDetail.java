package com.bookspot.batch.data.crawler;

import java.util.Objects;

public record LibraryNaruDetail(String name, String address, String naruDetail) {
    public LibraryNaruDetail {
        Objects.requireNonNull(name);
        Objects.requireNonNull(address);
        Objects.requireNonNull(naruDetail);
    }
}
