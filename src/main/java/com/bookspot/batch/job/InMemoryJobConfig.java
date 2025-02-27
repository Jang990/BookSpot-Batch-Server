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
    public Job isbnWarmUpJob(Step isbnIdWarmUpStep) {
        return new JobBuilder("isbnIdWarmUpJob", jobRepository)
                .start(isbnIdWarmUpStep)
                .build();
    }

    @Bean
    public Job isbnMemoryClearJob(Step isbnIdMemoryClearStep) {
        return new JobBuilder("isbnIdMemoryClearJob", jobRepository)
                .start(isbnIdMemoryClearStep)
                .build();
    }
}
