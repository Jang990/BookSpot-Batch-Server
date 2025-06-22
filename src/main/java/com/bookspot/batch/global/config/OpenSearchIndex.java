package com.bookspot.batch.global.config;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class OpenSearchIndex {
    public static final String SERVICE_ALIAS = "books";
    public static final String INDEX_PREFIX = SERVICE_ALIAS + "-";
    public static final String SCHEMA = """
            {
                "settings": {
                    "number_of_shards": 2,
                    "number_of_replicas": 0,
                    "analysis": {
                        // 토크나이저 정의
                        "tokenizer": {
                            "my_tokenizer": {
                                "type": "nori_tokenizer",
                                "decompound_mode": "mixed"
                            }
                        },
                        "analyzer": {
                                "my_nori_analyzer": {
                                    "type": "custom",
                                    "tokenizer": "my_tokenizer",
                                    "filter": ["nori_readingform", "nori_part_of_speech", "lowercase"]
                                }
                        }
                    }
                },
                "mappings": {
                    "dynamic": false,
                    "properties": {
                        "id": {
                            "type": "keyword"
                        },
                        "isbn13": {
                            "type": "keyword"
                        },
                        "title": {
                            "type": "text",
                            "analyzer": "my_nori_analyzer"
                        },
                        "subject_code": {
                            "type": "short"
                        },
                        "author": {
                            "type": "text",
                            "analyzer": "my_nori_analyzer"
                        },
                        "publication_year": {
                            "type": "short"
                        },
                        "publisher": {
                            "type": "keyword"
                        },
                        "loan_count": {
                            "type": "integer"
                        },
                        "library_ids": {
                            "type": "keyword"
                        },
                        "book_categories": {
                            "type": "keyword"
                        }
                    }
                }
            }
            """;

    public String serviceAlias() {
        return SERVICE_ALIAS;
    }

    private String indexName(LocalDate date) {
        return INDEX_PREFIX.concat(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
    }

    public String deletableIndexName() {
        return indexName(LocalDate.now().minusMonths(2));
    }

    public String backupIndexName() {
        return indexName(LocalDate.now().minusMonths(1));
    }

    public String serviceIndexName() {
        return indexName(LocalDate.now());
    }
}
