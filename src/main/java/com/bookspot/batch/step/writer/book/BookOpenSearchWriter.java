package com.bookspot.batch.step.writer.book;

import com.bookspot.batch.data.TEMP_BookDocument;
import com.bookspot.batch.global.config.OpenSearchIndex;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class BookOpenSearchWriter implements ItemWriter<TEMP_BookDocument> {
    private final OpenSearchClient openSearchClient;
    private final OpenSearchIndex openSearchIndex;

    @Override
    public void write(Chunk<? extends TEMP_BookDocument> chunk) throws Exception {
        final String indexName = openSearchIndex.indexName();

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (TEMP_BookDocument bookDocument : chunk) {
            br.operations(
                    op -> op.index(
                            idx -> idx.index(indexName)
                                    .id(bookDocument.getBookId())
                                    .document(bookDocument)
                    )
            );
        }

        openSearchClient.bulk(br.build());
    }
}
