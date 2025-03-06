package com.bookspot.batch.step.writer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.bookspot.batch.data.mapper.BookToDocumentMapper;
import com.bookspot.batch.step.writer.book.BookElasticSearchWriter;
import com.bookspot.batch.step.writer.book.UniqueBookInfoWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BookWriterConfig {
    private final DataSource dataSource;

    @Bean
    public UniqueBookInfoWriter bookInfoWriter() {
        return new UniqueBookInfoWriter(dataSource);
    }

    @Bean
    public BookElasticSearchWriter bookElasticSearchWriter(
            ElasticsearchClient elasticsearchClient,
            BookToDocumentMapper bookToDocumentMapper) {
        return new BookElasticSearchWriter(elasticsearchClient, bookToDocumentMapper);
    }
}
