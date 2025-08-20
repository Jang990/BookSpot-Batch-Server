package com.bookspot.batch.job;

import com.bookspot.batch.infra.opensearch.BookIndexSpec;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import org.opensearch.client.opensearch._types.OpenSearchException;

class OpenSearchTestHelper {

    static void deleteIfExist(OpenSearchRepository repository, String indexName) {
        try {
            repository.delete(indexName);
        } catch (OpenSearchException e) {}
    }

    static void createIndexIfExist(OpenSearchRepository repository, String indexName, String indexSpec) {
        try {
            repository.createIndex(indexName, indexSpec);
        } catch (RuntimeException e) {}
    }
}
