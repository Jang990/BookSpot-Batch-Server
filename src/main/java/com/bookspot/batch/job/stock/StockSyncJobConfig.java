package com.bookspot.batch.job.stock;

import com.bookspot.batch.job.listener.StockSyncJobListener;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StockSyncJobConfig {
    public static final String TEMP_DB_NAME = "temp_stock";
    public static final String SOURCE_DIR_PARAM_NAME = "sourceStockDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceStockDir']}";

    private final JobRepository jobRepository;
    private final CustomFilePathValidators filePathValidators;
    private final PlatformTransactionManager transactionManager;

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job stockSyncJob(
            Step stockStagingMasterStep,
            Step stockUpdateMasterStep,
            Step duplicatedTempStockMasterStep,
            Step stockInsertMasterStep) {
        return new JobBuilder("stockSyncJob", jobRepository)
                .start(createStockStagingStep())
                    .next(stockStagingMasterStep)
                    .next(stockUpdateMasterStep)
                    .next(duplicatedTempStockMasterStep)
                    .next(stockInsertMasterStep)
                .next(deleteStockStagingStep())
                .validator(
                        FilePathJobParameterValidator.REQUIRED_DIRECTORY(
                                filePathValidators,
                                SOURCE_DIR_PARAM_NAME
                        )
                )
                .listener(new StockSyncJobListener(jdbcTemplate))
                .build();
    }

    @Bean
    public Step createStockStagingStep() {
        return new StepBuilder("createStockStagingStep", jobRepository)
                .tasklet(
                        (contribution, chunkContext) -> {
                            jdbcTemplate.execute("""
                                    CREATE TABLE IF NOT EXISTS %s (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      book_id BIGINT NOT NULL,
                                      library_id BIGINT NOT NULL,
                                      PRIMARY KEY (id)
                                    ) ENGINE=InnoDB;
                                    """.formatted(TEMP_DB_NAME));
                            return RepeatStatus.FINISHED;
                        }, transactionManager
                )
                .build();
    }

    @Bean
    public Step deleteStockStagingStep() {
        return new StepBuilder("deleteStockStagingStep", jobRepository)
                .tasklet(
                        (contribution, chunkContext) -> {
                            jdbcTemplate.execute(
                                    "DROP TABLE IF EXISTS %s".formatted(TEMP_DB_NAME)
                            );
                            return RepeatStatus.FINISHED;
                        }, transactionManager
                )
                .build();
    }

}
