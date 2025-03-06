package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.reader.BookRepositoryReader;
import com.bookspot.batch.step.writer.book.BookElasticSearchWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookElasticSearchSyncStepConfig {
    private static final int CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step bookElasticSearchSyncStep(
            BookRepositoryReader bookRepositoryReader,
            BookElasticSearchWriter bookElasticSearchWriter) {
        return new StepBuilder("bookElasticSearchSyncStep", jobRepository)
                .<ConvertedUniqueBook, ConvertedUniqueBook>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookRepositoryReader)
                .writer(bookElasticSearchWriter)
                .build();
    }
}
