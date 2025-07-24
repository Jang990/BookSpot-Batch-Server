package com.bookspot.batch.infra.opensearch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record BookIndexSpec(LocalDate base) {
    private static final String SERVICE_ALIAS = "books";
    private static final String INDEX_PREFIX = SERVICE_ALIAS + "-";
    public static final String SCHEMA = """
            {
                "settings": {
                    "number_of_shards": 2,
                    "number_of_replicas": 0,
                    "analysis": {
                        "tokenizer": {
                            "my_tokenizer": {
                                "type": "nori_tokenizer",
                                "decompound_mode": "mixed"
                            },
                            "edge_ngram_tokenizer": {
                                "type": "edge_ngram",
                                "min_gram": 2,
                                "max_gram": 5
                            }
                        },
                        "analyzer": {
                                "my_nori_analyzer": {
                                    "type": "custom",
                                    "tokenizer": "my_tokenizer",
                                    "filter": ["nori_readingform", "nori_part_of_speech", "lowercase"]
                                },
                                "my_ngram_analyzer": {
                                    "type": "custom",
                                    "tokenizer": "edge_ngram_tokenizer",
                                    "filter": ["lowercase"]
                                }
                        }
                    }
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
                            "type": "text",
                            "analyzer": "my_nori_analyzer",
                            "fields": {
                                "keyword": {
                                    "type": "keyword"
                                },
                                "ngram": {
                                    "type": "text",
                                    "analyzer": "my_ngram_analyzer"
                                }
                            }
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
                        },
                        "created_at": {
                            "type": "date",
                            "format": "yyyy-MM-dd"
                        }
                    }
                }
            }
            """;

    private String indexName(LocalDate date) {
        return INDEX_PREFIX.concat(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
    }

    public String serviceAlias() {
        return SERVICE_ALIAS;
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
