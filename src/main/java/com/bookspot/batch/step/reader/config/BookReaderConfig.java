package com.bookspot.batch.step.reader.config;

import com.bookspot.batch.job.listener.BookSyncJobListener;
import com.bookspot.batch.job.listener.StockSyncJobListener;
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
                BookSyncJobListener.ISBN_WARMUP_SIZE
        );
    }

    @Bean
    public IsbnIdReader isbnIdReader(
            DataSource dataSource,
            IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory) throws Exception {
        return new IsbnIdReader(
                dataSource,
                isbnIdPagingQueryProviderFactory.getObject(),
                StockSyncJobListener.ISBN_ID_WARMUP_SIZE
        );
    }

    @Bean
    public IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory(DataSource dataSource) {
        return new IsbnIdPagingQueryProviderFactory(dataSource);
    }
}
