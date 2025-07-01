package com.bookspot.batch.infra.opensearch;

import com.bookspot.batch.global.config.OpenSearchIndex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record IndexName(LocalDate base) {
    private String indexName(LocalDate date) {
        return OpenSearchIndex.INDEX_PREFIX.concat(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
    }

    public String deletableIndexName() {
        return indexName(base.minusMonths(2));
    }

    public String backupIndexName() {
        return indexName(base.minusMonths(1));
    }

    public String serviceIndexName() {
        return indexName(base);
    }
}
