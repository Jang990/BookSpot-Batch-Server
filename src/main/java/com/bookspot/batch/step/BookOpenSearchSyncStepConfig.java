package com.bookspot.batch.step;

import com.bookspot.batch.data.TEMP_BookDocument;
import com.bookspot.batch.global.config.OpenSearchIndex;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.reader.BookWithLibraryIdReader;
import com.bookspot.batch.step.service.LibraryStockRepository;
import com.bookspot.batch.step.service.BookRepository;
import com.bookspot.batch.step.writer.book.BookOpenSearchWriter;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookOpenSearchSyncStepConfig {
    private static final int CHUNK_SIZE = 500;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StepLoggingListener stepLoggingListener;

    private final BookRepository bookRepository;
    private final LibraryStockRepository libraryStockRepository;

    private final OpenSearchClient openSearchClient;
    private final OpenSearchIndex openSearchIndex;


    @Bean
    public Step bookOpenSearchSyncStep() throws Exception {
        return new StepBuilder("bookOpenSearchSyncStep", jobRepository)
                .<TEMP_BookDocument, TEMP_BookDocument>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookWithLibraryIdReader())
                .writer(bookOpenSearchWriter())
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public BookWithLibraryIdReader bookWithLibraryIdReader() {
        return new BookWithLibraryIdReader(
                bookRepository,
                libraryStockRepository,
                CHUNK_SIZE
        );
    }

    @Bean
    public BookOpenSearchWriter bookOpenSearchWriter() {
        return new BookOpenSearchWriter(openSearchClient, openSearchIndex);
    }
}
