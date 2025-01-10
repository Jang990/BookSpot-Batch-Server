package com.bookspot.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job libraryJob(Step libraryStep) {
        return new JobBuilder("libraryJob2", jobRepository)
                .start(libraryStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
