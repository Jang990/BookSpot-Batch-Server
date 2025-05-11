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
public class StockSyncJobConfig {
    public static final String SOURCE_DIR_PARAM_NAME = "sourceDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceDir']}";

    public static final String DELETE_DIR_PARAM_NAME = "deleteDir";
    public static final String DELETE_DIR_PARAM = "#{jobParameters['deleteDir']}";

    public static final int STEP_CHUNK_SIZE = 6_000;

    private final JobRepository jobRepository;

    @Bean
    public Job stockSyncJob(
            Step insertStockMasterStep,
            Step deleteStockFileMasterStep,
            Step deleteStockMasterStep) {
        return new JobBuilder("stockSyncJob", jobRepository)
                .start(insertStockMasterStep) // 6000 - 2 | 8m50s390ms
                .next(deleteStockFileMasterStep) // 6000 - 2 | 8m36s647ms
                .next(deleteStockMasterStep) // 6000 - 2 | 4s11ms
                .build();
    }

}
