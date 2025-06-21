package com.bookspot.batch.step;

import com.bookspot.batch.data.BookCode;
import com.bookspot.batch.data.BookDocument;
import com.bookspot.batch.global.config.OpenSearchIndex;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.reader.BookWithLibraryIdReader;
import com.bookspot.batch.step.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final BookCodeRepository bookCodeRepository;


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
                bookCodeResolver(),
                CHUNK_SIZE
        );
    }

    @Bean
    @StepScope
    public BookCodeResolver bookCodeResolver() {
        List<BookCode> bookCodes = bookCodeRepository.findAll();
        Map<Integer, String> bookCodeMap = new HashMap<>();
        for (BookCode bookCode : bookCodes)
            bookCodeMap.put(bookCode.getId(), bookCode.getName());
        return new BookCodeResolver(bookCodeMap);
    }
}
