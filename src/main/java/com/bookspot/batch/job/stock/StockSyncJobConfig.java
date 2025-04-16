package com.bookspot.batch.job.stock;

import com.bookspot.batch.global.file.stock.StockFileManager;
import com.bookspot.batch.job.listener.StockSyncJobListener;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.temp_FilePathJobParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StockSyncJobConfig {
    public static final String SOURCE_DIR_PARAM_NAME = "sourceDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceDir']}";

    private final JobRepository jobRepository;
    private final CustomFilePathValidators filePathValidators;

    private final StockFileManager stockFileManager;

    @Bean
    public Job stockSyncJob(
            StockSyncJobListener stockSyncJobListener,

            Step isbnIdMapInitStep,
            Step stockSyncPartitionMasterStep,
            Step missingStockDeleteStep,
            Step stockUpdatedAtStep,
            Step isbnIdMapCleaningStep) {
        // 재고 업데이트 -> 사라진 재고 정보 삭제 -> 크롤링 시점 업데이트
        return new JobBuilder("stockFileJob", jobRepository)
                .start(isbnIdMapInitStep)
                    .next(stockSyncPartitionMasterStep)
                    .next(missingStockDeleteStep)
                    .next(stockUpdatedAtStep)
                .next(isbnIdMapCleaningStep)
                .listener(stockSyncJobListener)
                .validator(
                        temp_FilePathJobParameterValidator.REQUIRED_DIRECTORY(
                                filePathValidators,
                                SOURCE_DIR_PARAM_NAME
                        )
                )
                .build();
    }

    @Bean
    @JobScope
    public StockSyncJobListener stockSyncJobListener(@Value(SOURCE_DIR_PARAM) String sourceDirPath) {
        return new StockSyncJobListener(stockFileManager, sourceDirPath);
    }
}
