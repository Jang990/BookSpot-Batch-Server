package com.bookspot.batch.job;

import com.bookspot.batch.infra.opensearch.IndexName;
import com.bookspot.batch.infra.opensearch.IndexNameCreator;
import com.bookspot.batch.infra.opensearch.OpenSearchIndex;
import com.bookspot.batch.job.extractor.CommonStringJobParamExtractor;
import com.bookspot.batch.job.listener.alert.AlertJobListener;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class BookOpenSearchSyncJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final OpenSearchIndex openSearchIndex;
    private final OpenSearchRepository openSearchRepository;
    private final IndexNameCreator indexNameCreator;

    @Bean
    public Step bookOpenSearchSyncJobStep(Job bookOpenSearchSyncJob, JobLauncher jobLauncher) {
        return new StepBuilder("bookOpenSearchSyncJobStep", jobRepository)
                .job(bookOpenSearchSyncJob)
                .launcher(jobLauncher)
                .parametersExtractor(bookOpenSearchSyncJobParamExtractor())
                .build();
    }


    @Bean
    public JobParametersExtractor bookOpenSearchSyncJobParamExtractor() {
        return CommonStringJobParamExtractor.EmptyExtractor;
    }

    @Bean
    public Job bookOpenSearchSyncJob(
            AlertJobListener alertJobListener,
            Step bookOpenSearchSyncStep
    ) {
        return new JobBuilder("bookOpenSearchSyncJob", jobRepository)
                .start(createIndexStep())
                .next(bookOpenSearchSyncStep)
                .next(cleanIndexStep())
                .listener(alertJobListener)
                .build();
    }

    @Bean
    public Step createIndexStep() {
        return new StepBuilder("createIndexStep", jobRepository)
                .tasklet(createIndexTasklet(null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet createIndexTasklet(
            @Value(BookSpotParentJobConfig.MONTH_PARAM) LocalDate baseDate
    ) {
        return (contribution, chunkContext) -> {
            IndexName indexName = indexNameCreator.create(baseDate);
            openSearchRepository.createIndex(indexName.serviceIndexName(), OpenSearchIndex.SCHEMA);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step cleanIndexStep() {
        return new StepBuilder("cleanIndexStep", jobRepository)
                .tasklet(changeIndexTasklet(null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet changeIndexTasklet(
            @Value(BookSpotParentJobConfig.MONTH_PARAM) LocalDate baseDate
    ) {
        return (contribution, chunkContext) -> {
            IndexName indexName = indexNameCreator.create(baseDate);
            openSearchRepository.delete(indexName.deletableIndexName());
            openSearchRepository.addAlias(indexName.serviceIndexName(), indexName.serviceAlias());
            openSearchRepository.removeAlias(indexName.backupIndexName(), indexName.serviceAlias());
            return RepeatStatus.FINISHED;
        };
    }
}
