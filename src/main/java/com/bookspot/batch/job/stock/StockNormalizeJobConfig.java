package com.bookspot.batch.job.stock;

import com.bookspot.batch.job.JobParameterFileService;
import com.bookspot.batch.job.decider.FileCreationStatusDecider;
import com.bookspot.batch.job.loan.AggregationCompletedDecider;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.StockNormalizeStepConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class StockNormalizeJobConfig {
    private final JobRepository jobRepository;
    private final CustomFilePathValidators filePathValidators;

    public static final String SOURCE_DIR_PARAM_NAME = "sourceDir";
    public static final String SOURCE_DIR_PARAM = "#{jobParameters['sourceDir']}";

    public static final String NORMALIZE_DIR_PARAM_NAME = "normalizeDir";
    public static final String NORMALIZE_DIR_PARAM = "#{jobParameters['normalizeDir']}";

    public static final String DUPLICATED_FILTER_DIR_PARAM_NAME = "filteredDir";
    public static final String DUPLICATED_FILTER_DIR_PARAM = "#{jobParameters['filteredDir']}";

    @Bean
    public Job stockNormalizeJob(Flow stockCleansingFlow) {
        return new JobBuilder("stockNormalizeJob", jobRepository)
                .validator(
                        FilePathJobParameterValidator.of(
                                filePathValidators,
                                Map.of(
                                        SOURCE_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY,

                                        NORMALIZE_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY,

                                        DUPLICATED_FILTER_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY
                                )
                        )
                )
                .start(stockCleansingFlow)
                .end()
                .build();
    }

    @Bean
    public Flow stockCleansingFlow(
            FileCreationStatusDecider stockCleansingFileDecider,
            Step isbnIdMapInitStep,
            Step stockNormalizeMasterStep,
            Step isbnIdMapCleaningStep,
            Step duplicatedBookFilterMasterStep
    ) {
        return new FlowBuilder<Flow>("stockCleansingFlow")
                .start(stockCleansingFileDecider)
                    .on(FileCreationStatusDecider.EXECUTE_ALL)
                        .to(isbnIdMapInitStep)
                        .next(stockNormalizeMasterStep)
                        .next(isbnIdMapCleaningStep)
                        .next(duplicatedBookFilterMasterStep)
                .from(stockCleansingFileDecider)
                    .on(FileCreationStatusDecider.SKIP_AGGREGATION)
                        .to(duplicatedBookFilterMasterStep)
                .build();
    }

    @Bean
    public FileCreationStatusDecider stockCleansingFileDecider(JobExplorer jobExplorer) {
        return new FileCreationStatusDecider(
                StockNormalizeStepConfig.STOCK_NORMALIZE_MASTER_STEP,
                jobExplorer
        );
    }
}
