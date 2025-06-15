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
    public static final String DOWNLOAD_DIR_PARAM_NAME = "downloadDir";
    public static final String STOCK_DIR_PARAM_NAME = "libraryBookDir";
    public static final String LOAN_OUTPUT_FILE_PARAM_NAME = "loanOutputFilePath";

    public static final String CLEANSING_DIR_PARAM_NAME = "cleansingDir";
    public static final String DUPLICATED_FILTER_DIR_PARAM_NAME = "filteredDir";
    public static final String DELETE_DIR_PARAM_NAME = "deleteDir";

    @Bean
    public Job bookSpotParentJob(
            Step librarySyncJobStep,
            Step stockFileJobStep,
            Step bookSyncJobStep,
            Step loanAggregatedJobStep,
            Step stockSyncJobStep,
            Step bookOpenSearchSyncJobStep
    ) {
        return new JobBuilder("bookSpotParentJob", jobRepository)
                .start(librarySyncJobStep)
                .next(stockFileJobStep)
                .next(bookSyncJobStep)
                .next(loanAggregatedJobStep)
                .next(stockSyncJobStep)
                .next(bookOpenSearchSyncJobStep)
                .build();
    }


}
