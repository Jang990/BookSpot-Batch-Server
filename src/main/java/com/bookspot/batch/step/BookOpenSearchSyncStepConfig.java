package com.bookspot.batch.step;

import com.bookspot.batch.data.BookCode;
import com.bookspot.batch.data.BookDocument;
import com.bookspot.batch.infra.opensearch.*;
import com.bookspot.batch.job.BookSpotParentJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.reader.BookWithLibraryIdReader;
import com.bookspot.batch.step.service.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BookOpenSearchSyncStepConfig {
    private static final int CHUNK_SIZE = 150;
    private static final int RETRY_LIMIT = 5;
    private static final long BACK_OFF_DELAY = 250L;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StepLoggingListener stepLoggingListener;

    private final EntityManager entityManager;
    private final LibraryStockRepository libraryStockRepository;

    private final OpenSearchRepository openSearchRepository;
    private final BookCodeRepository bookCodeRepository;
    private final IndexSpecCreator indexSpecCreator;


    @Bean
    public Step bookOpenSearchSyncStep(AlertStepListener alertStepListener) throws Exception {
        return new StepBuilder("bookOpenSearchSyncStep", jobRepository)
                .<BookDocument, BookDocument>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookWithLibraryIdReader())
                .writer(bookDocumentItemWriter(null))
                .listener(stepLoggingListener)
                .listener(alertStepListener)
                .faultTolerant()
                .retry(OpenSearch504Exception.class)
                .retryLimit(RETRY_LIMIT)
                .backOffPolicy(openSearchFixedBackOffPolicy())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<BookDocument> bookDocumentItemWriter(
            @Value(BookSpotParentJobConfig.MONTH_PARAM) LocalDate baseDate
    ) {
        BookIndexSpec bookIndexSpec = indexSpecCreator.create(baseDate);
        return chunk -> openSearchRepository.save(
                bookIndexSpec.serviceIndexName(), chunk.getItems()
        );
    }

    @Bean
    public FixedBackOffPolicy openSearchFixedBackOffPolicy() {
        FixedBackOffPolicy openSearchFixedBackOffPolicy = new FixedBackOffPolicy();
        openSearchFixedBackOffPolicy.setBackOffPeriod(BACK_OFF_DELAY);
        return openSearchFixedBackOffPolicy;
    }

    @Bean
    @StepScope
    public BookWithLibraryIdReader bookWithLibraryIdReader() {
        return new BookWithLibraryIdReader(
                entityManager,
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
