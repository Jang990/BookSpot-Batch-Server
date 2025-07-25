package com.bookspot.batch.job.stock;

import com.bookspot.batch.job.BookSpotParentJobConfig;
import com.bookspot.batch.job.extractor.CommonStringJobParamExtractor;
import com.bookspot.batch.job.listener.alert.AlertJobListener;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class StockSyncJobConfig {

    public static final String SOURCE_DIR_PARAM_NAME = "sourceDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceDir']}";

    public static final String CLEANSING_DIR_PARAM_NAME = "cleansingDir";
    public static final String CLEANSING_DIR_PARAM = "#{jobParameters['cleansingDir']}";

    public static final String DUPLICATED_FILTER_DIR_PARAM_NAME = "filteredDir";
    public static final String DUPLICATED_FILTER_DIR_PARAM = "#{jobParameters['filteredDir']}";

    public static final String DELETE_DIR_PARAM_NAME = "deleteDir";
    public static final String DELETE_DIR_PARAM = "#{jobParameters['deleteDir']}";

    // CPU 사용량 3~7%라서 청크 사이즈 올리기 => GC 부하 견딜만 함.
    // long-long이라 메모리 부하도 적은 작업 => 메모리 부하도 크지 않음
    // Insert도 파일당 평균 800건 정도 => 때문에 DB 부담도 크지 않음.
    public static final int INSERT_CHUNK_SIZE = 15_000;
    public static final int DELETE_FILE_CHUNK_SIZE = 15_000;
    public static final int DELETE_CHUNK_SIZE = 5_000;

    private final CustomFilePathValidators filePathValidators;
    private final JobRepository jobRepository;

    @Bean
    public Step stockSyncJobStep(Job stockSyncJob, JobLauncher jobLauncher) {
        return new StepBuilder("stockSyncJobStep", jobRepository)
                .job(stockSyncJob)
                .launcher(jobLauncher)
                .parametersExtractor(stockSyncJobParamExtractor())
                .build();
    }


    @Bean
    public JobParametersExtractor stockSyncJobParamExtractor() {
        return new CommonStringJobParamExtractor(
                Map.of(
                        BookSpotParentJobConfig.STOCK_DIR_PARAM_NAME,
                        SOURCE_DIR_PARAM_NAME,

                        BookSpotParentJobConfig.CLEANSING_DIR_PARAM_NAME,
                        CLEANSING_DIR_PARAM_NAME,

                        BookSpotParentJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                        DUPLICATED_FILTER_DIR_PARAM_NAME,

                        BookSpotParentJobConfig.DELETE_DIR_PARAM_NAME,
                        DELETE_DIR_PARAM_NAME
                )
        );
    }

    @Bean
    public Job stockSyncJob(
            AlertJobListener alertJobListener,
            Step stockCleansingMasterStep,
            Step duplicatedBookFilterMasterStep,
            Step insertStockMasterStep,
            Step deleteStockFileMasterStep,
            Step deleteStockMasterStep) {
        return new JobBuilder("stockSyncJob", jobRepository)
                .start(stockCleansingMasterStep)
                .next(duplicatedBookFilterMasterStep)
                .next(insertStockMasterStep) // 10_000 - 2 | 6m12s543ms  //  6000 | 8m59s362ms // 15000 | 5m8s821ms | 10.7%
                .next(deleteStockFileMasterStep) // 6000 - 2 | 8m36s647ms
                .next(deleteStockMasterStep) // 6000 - 2 | 4s11ms
                .listener(alertJobListener)
                .validator(
                        FilePathJobParameterValidator.of(
                                filePathValidators,
                                Map.of(
                                        SOURCE_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY,

                                        CLEANSING_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY,

                                        DUPLICATED_FILTER_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY,

                                        DELETE_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY
                                )
                        )
                )
                .build();
    }

}
