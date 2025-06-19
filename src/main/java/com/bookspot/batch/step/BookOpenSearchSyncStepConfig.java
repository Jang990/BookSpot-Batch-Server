package com.bookspot.batch.step;

import com.bookspot.batch.data.BookDocument;
import com.bookspot.batch.global.config.OpenSearchIndex;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.reader.BookWithLibraryIdReader;
import com.bookspot.batch.step.service.LibraryStockRepository;
import com.bookspot.batch.step.service.BookRepository;
import com.bookspot.batch.step.service.OpenSearchRepository;
import lombok.RequiredArgsConstructor;
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
    private static final int CHUNK_SIZE = 300;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StepLoggingListener stepLoggingListener;

    private final BookRepository bookRepository;
    private final LibraryStockRepository libraryStockRepository;

    private final OpenSearchIndex openSearchIndex;
    private final OpenSearchRepository openSearchRepository;


    @Bean
    public Step bookOpenSearchSyncStep() throws Exception {
        return new StepBuilder("bookOpenSearchSyncStep", jobRepository)
                .<BookDocument, BookDocument>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookWithLibraryIdReader())
                .writer(chunk -> openSearchRepository.save(openSearchIndex.serviceIndexName(), chunk.getItems()))
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
}
