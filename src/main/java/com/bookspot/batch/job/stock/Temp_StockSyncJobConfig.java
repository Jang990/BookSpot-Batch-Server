package com.bookspot.batch.job.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class Temp_StockSyncJobConfig {
    public static final String SOURCE_DIR_PARAM_NAME = "sourceDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceDir']}";

    public static final String INSERT_DIR_PARAM_NAME = "insertDir";
    public static final String INSERT_DIR_PARAM = "#{jobParameters['insertDir']}";


    private final JobRepository jobRepository;

    @Bean
    public Job temp_stockSyncJob(Step insertStockMasterStep) {
        return new JobBuilder("temp_stockSyncJob", jobRepository)
                .start(insertStockMasterStep)
                .build();
    }
}
