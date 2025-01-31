package com.bookspot.batch.step;

import com.bookspot.batch.step.processor.csv.stock.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class LibraryStockUpdatedAtStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final LibraryRepository libraryRepository;

    @Bean
    public Step libraryStockUpdatedAtStep() {
        return new StepBuilder("libraryStockUpdatedAtStep", jobRepository)
                .tasklet(libraryStockUpdatedAtTasklet(null, null), platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet libraryStockUpdatedAtTasklet(
            @Value("#{jobParameters['libraryId']}") Long libraryId,
            @Value("#{jobParameters['referenceDate']}") LocalDate referenceDate) {
        return (contribution, chunkContext) -> {
            libraryRepository.updateStockDate(libraryId, referenceDate);
            return RepeatStatus.FINISHED;
        };
    }
}
