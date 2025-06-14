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
public class BookSpotParentJobConfig {
    private final JobRepository jobRepository;

    public static final String MONTH_PARAM_NAME = "month";
    public static final String MONTH_PARAM = "#{jobParameters['month']}";

    public static final String LIBRARY_FILE_PARAM_NAME = "libraryFilePath";
    public static final String LIBRARY_FILE_PARAM = "#{jobParameters['libraryFilePath']}";

    public static final String STOCK_DIR_PARAM_NAME = "libraryBookDir";
    public static final String STOCK_DIR_PARAM = "#{jobParameters['libraryBookDir']}";

    @Bean
    public Job bookSpotParentJob(Step bookSyncJobStep) {
        return new JobBuilder("bookSpotParentJob", jobRepository)
                .start(bookSyncJobStep)
                .build();
    }


}
