package com.bookspot.batch.infra.opensearch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record BookRankingIndexSpec() {
    public static final String SCHEMA = """
            {
                "settings": {
                    "number_of_shards": 1,
                    "number_of_replicas": 0
                },
                "mappings": {
                    "dynamic": false,
                    "properties": {
                        "book_id": {
                            "type": "keyword"
                        },
                        "isbn13": {
                            "type": "keyword"
                        },
                        "title": {
                            "type": "keyword"
                        },
                        "subject_code": {
                            "type": "short"
                        },
                        "author": {
                            "type": "keyword"
                        },
                        "publication_year": {
                            "type": "short"
                        },
                        "publisher": {
                            "type": "keyword"
                        },
                        "created_at": {
                            "type": "date",
                            "format": "yyyy-MM-dd"
                        },
                        "rank": {
                            "type": "short"
                        },
                        "ranking_date": {
                            "type": "date",
                            "format": "yyyy-MM-dd"
                        },
                        "loan_increase": {
                            "type": "integer"
                        },
                        "ranking_type": {
                            "type": "keyword"
                        },
                        "ranking_age": {
                            "type": "keyword"
                        },
                        "ranking_gender": {
                            "type": "keyword"
                        }
                    }
                }
            }
            """;

    public String serviceIndexName() {
        return "books-ranking";
    }

    public String dailyIndexName(LocalDate referenceDate) {
        return "daily-books-ranking" + "-" + referenceDate;
    }

    public String dailyAliasName() {
        return "daily-books-ranking";
    }
}