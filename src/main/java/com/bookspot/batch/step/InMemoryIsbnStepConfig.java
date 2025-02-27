package com.bookspot.batch.step;

import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class InMemoryIsbnStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    /*@Bean
    @StepScope
    public Step isbnMemoryClearStep() {
        return new StepBuilder("isbnIdMemoryClearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    isbnEclipseMemoryRepository.clearMemory();
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step isbnWarmUpStep() throws Exception {
        return new StepBuilder("isbnIdWarmUpStep", jobRepository)
                .<Isbn13MemoryData, Isbn13MemoryData>chunk(WARM_UP_CHUNK_SIZE, platformTransactionManager)
                .reader(isbnReader())
                .writer(isbnWriter())
                .allowStartIfComplete(true)
                .build();
    }*/
}
