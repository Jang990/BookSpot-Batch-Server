package com.bookspot.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BookElasticSearchSyncJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job bookElasticSearchSyncJob(Step bookElasticSearchSyncStep) {
        return new JobBuilder("bookElasticSearchSyncJob", jobRepository)
                .start(bookElasticSearchSyncStep)
                .build();
    }
}
