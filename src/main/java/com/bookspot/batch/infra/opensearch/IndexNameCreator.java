package com.bookspot.batch.infra.opensearch;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class IndexNameCreator {
    public IndexName create(LocalDate date) {
        return new IndexName(date);
    }
}
