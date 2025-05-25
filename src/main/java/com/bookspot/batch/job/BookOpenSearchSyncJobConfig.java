package com.bookspot.batch.job;

import com.bookspot.batch.global.config.OpenSearchIndex;
import com.bookspot.batch.step.service.OpenSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookOpenSearchSyncJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final OpenSearchIndex openSearchIndex;
    private final OpenSearchRepository openSearchRepository;

    @Bean
    public Job bookOpenSearchSyncJob(Step bookOpenSearchSyncStep) {
        return new JobBuilder("bookOpenSearchSyncJob", jobRepository)
                .start(createIndexStep())
                .next(bookOpenSearchSyncStep)
                .next(cleanIndexStep())
                .build();
    }

    @Bean
    public Step createIndexStep() {
        return new StepBuilder("createIndexStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    openSearchRepository.createIndex(openSearchIndex.serviceIndexName(), OpenSearchIndex.SCHEMA);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step cleanIndexStep() {
        return new StepBuilder("cleanIndexStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    openSearchRepository.delete(openSearchIndex.deletableIndexName());
                    openSearchRepository.addAlias(
                            openSearchIndex.serviceIndexName(),
                            openSearchIndex.serviceAlias()
                    );
                    openSearchRepository.removeAlias(
                            openSearchIndex.backupIndexName(),
                            openSearchIndex.serviceAlias()
                    );
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
