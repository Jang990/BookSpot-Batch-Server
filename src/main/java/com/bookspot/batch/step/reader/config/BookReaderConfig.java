package com.bookspot.batch.step.reader.config;

import com.bookspot.batch.step.InMemoryIsbnIdStepConfig;
import com.bookspot.batch.step.InMemoryIsbnStepConfig;
import com.bookspot.batch.step.reader.BookRepositoryReader;
import com.bookspot.batch.step.reader.IsbnIdPagingQueryProviderFactory;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.service.UniqueBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BookReaderConfig {
    @Bean
    public BookRepositoryReader bookRepositoryReader(UniqueBookRepository uniqueBookRepository) {
        return new BookRepositoryReader(uniqueBookRepository, 1_000);
    }

    @Bean
    public IsbnReader isbnReader(
            DataSource dataSource,
            IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory) throws Exception {
        return new IsbnReader(
                dataSource,
                isbnIdPagingQueryProviderFactory.getObject(),
                InMemoryIsbnStepConfig.CHUNK_SIZE
        );
    }

    @Bean
    public IsbnIdReader isbnIdReader(
            DataSource dataSource,
            IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory) throws Exception {
        return new IsbnIdReader(
                dataSource,
                isbnIdPagingQueryProviderFactory.getObject(),
                InMemoryIsbnIdStepConfig.WARM_UP_CHUNK_SIZE
        );
    }

    @Bean
    public IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory(DataSource dataSource) {
        return new IsbnIdPagingQueryProviderFactory(dataSource);
    }
}
