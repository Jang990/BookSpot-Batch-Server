package com.bookspot.batch.infra.opensearch;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class IndexSpecCreator {
    public BookIndexSpec create(LocalDate date) {
        return new BookIndexSpec(date);
    }
}
