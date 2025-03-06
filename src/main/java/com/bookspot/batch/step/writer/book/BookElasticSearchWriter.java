package com.bookspot.batch.step.writer.book;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.mapper.BookToDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class BookElasticSearchWriter implements ItemWriter<ConvertedUniqueBook> {
    private final ElasticsearchClient elasticsearchClient;
    private final BookToDocumentMapper documentMapper;

    @Override
    public void write(Chunk<? extends ConvertedUniqueBook> chunk) throws Exception {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (ConvertedUniqueBook convertedUniqueBook : chunk) {
            br.operations(op -> op.index(
                    idx -> idx.index("my_books")
                            .id(convertedUniqueBook.getId().toString())
                            .document(documentMapper.transform(convertedUniqueBook))
                    )
            );
        }

        elasticsearchClient.bulk(br.build());
    }
}
