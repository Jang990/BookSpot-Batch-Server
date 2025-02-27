package com.bookspot.batch.step;

import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class InMemoryIsbnStepConfig {
    public static final int CHUNK_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final IsbnSet isbnSet;

    @Bean
    public Step inMemoryIsbnClearStep() {
        return new StepBuilder("inMemoryIsbnClearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    isbnSet.clearAll();
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step inMemoryIsbnWarmUpStep(
            IsbnReader isbnReader,
            InMemoryIsbnWriter inMemoryIsbnWriter) {
        return new StepBuilder("inMemoryIsbnWarmUpStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(isbnReader)
                .writer(inMemoryIsbnWriter)
                .allowStartIfComplete(true)
                .build();
    }

}
