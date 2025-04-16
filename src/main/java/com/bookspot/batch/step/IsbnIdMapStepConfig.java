package com.bookspot.batch.step;

import com.bookspot.batch.step.reader.IsbnIdPagingQueryProviderFactory;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
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
public class IsbnIdMapStepConfig {
    public static final int ISBN_ID_WARMUP_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final IsbnMemoryRepository isbnMemoryRepository;


    @Bean
    public Step isbnIdMapInitStep(IsbnIdReader isbnIdReader) {
        return new StepBuilder("isbnIdMapInitStep", jobRepository)
                .<Isbn13MemoryData, Isbn13MemoryData>chunk(ISBN_ID_WARMUP_SIZE, transactionManager)
                .reader(isbnIdReader)
                .writer(
                        chunk -> {
                            for (Isbn13MemoryData data : chunk)
                                isbnMemoryRepository.add(data);
                        }
                )
                .build();
    }

    @Bean
    public Step isbnIdMapCleaningStep() {
        return new StepBuilder("isbnIdMapCleaningStep", jobRepository)
                .tasklet(
                        (contribution, chunkContext) -> {
                            isbnMemoryRepository.clearMemory();
                            return RepeatStatus.FINISHED;
                        },
                        transactionManager
                )
                .build();
    }

}
