package com.bookspot.batch.job.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DuplicatedBookFilterJobConfig {
    public static final String SOURCE_DIR_PARAM_NAME = "sourceDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceDir']}";

    public static final String OUTPUT_DIR_PARAM_NAME = "filteredDir";
    public static final String OUTPUT_DIR_PARAM = "#{jobParameters['filteredDir']}";

    private final JobRepository jobRepository;

    @Bean
    public Job duplicatedBookFilterJob(Step duplicatedBookFilterMasterStep) {
        return new JobBuilder("duplicatedBookFilterJob", jobRepository)
                .start(duplicatedBookFilterMasterStep)
                .build();
    }
}
