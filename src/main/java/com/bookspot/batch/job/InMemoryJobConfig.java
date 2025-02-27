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
public class InMemoryJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job isbnIdWarmUpJob(Step inMemoryIsbnIdWarmUpStep) {
        return new JobBuilder("isbnIdWarmUpJob", jobRepository)
                .start(inMemoryIsbnIdWarmUpStep)
                .build();
    }

    @Bean
    public Job isbnIdMemoryClearJob(Step inMemoryIsbnIdClearStep) {
        return new JobBuilder("isbnIdMemoryClearJob", jobRepository)
                .start(inMemoryIsbnIdClearStep)
                .build();
    }
}
