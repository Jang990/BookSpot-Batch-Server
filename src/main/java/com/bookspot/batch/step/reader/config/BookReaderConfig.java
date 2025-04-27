package com.bookspot.batch.step.reader.config;

import com.bookspot.batch.step.reader.BookRepositoryReader;
import com.bookspot.batch.step.reader.IsbnIdPagingQueryProviderFactory;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.service.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BookReaderConfig {
    private static final int WARM_UP_SIZE = 5_000;

    @Bean
    public BookRepositoryReader bookRepositoryReader(BookRepository bookRepository) {
        return new BookRepositoryReader(bookRepository, 1_000);
    }

    @Bean
    public IsbnReader isbnReader(
            DataSource dataSource,
            IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory) throws Exception {
        return new IsbnReader(
                dataSource,
                isbnIdPagingQueryProviderFactory.getObject(),
                WARM_UP_SIZE
        );
    }

    @Bean
    @StepScope
    public IsbnIdReader isbnIdReader(
            DataSource dataSource,
            IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory) throws Exception {
        return new IsbnIdReader(
                dataSource,
                isbnIdPagingQueryProviderFactory.getObject(),
                WARM_UP_SIZE
        );
    }

    @Bean
    public IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory(DataSource dataSource) {
        return new IsbnIdPagingQueryProviderFactory(dataSource);
    }
}
