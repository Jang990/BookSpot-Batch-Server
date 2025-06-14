package com.bookspot.batch.job.loan;

import com.bookspot.batch.job.BookSpotParentJobConfig;
import com.bookspot.batch.job.extractor.CommonStringJobParamExtractor;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
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
public class LoanAggregatedJobConfig {
    public static final String DIRECTORY_PARAM_NAME = "outputDirectory";
    public static final String DIRECTORY_PATH = "#{jobParameters['outputDirectory']}";

    public static final String OUTPUT_FILE_PARAM_NAME = "outputFilePath";
    public static final String OUTPUT_FILE_PATH = "#{jobParameters['outputFilePath']}";

    private final JobRepository jobRepository;

    private final CustomFilePathValidators filePathValidators;

    @Bean
    public Step loanAggregatedJobStep(Job loanAggregatedJob, JobLauncher jobLauncher) {
        return new StepBuilder("loanAggregatedJobStep", jobRepository)
                .job(loanAggregatedJob)
                .launcher(jobLauncher)
                .parametersExtractor(loanAggregatedJobParamExtractor())
                .build();
    }


    @Bean
    public JobParametersExtractor loanAggregatedJobParamExtractor() {
        return new CommonStringJobParamExtractor(
                Map.of(
                        BookSpotParentJobConfig.STOCK_DIR_PARAM_NAME,
                        DIRECTORY_PARAM_NAME,

                        BookSpotParentJobConfig.LOAN_OUTPUT_FILE_PARAM_NAME,
                        OUTPUT_FILE_PARAM_NAME
                )
        );
    }

    @Bean
    public Job loanAggregatedJob(
            Step readLoanCountMasterStep,
            Step syncLoanCountStep
    ) {
        return new JobBuilder("loanAggregatedJob", jobRepository)
                .validator(
                        FilePathJobParameterValidator.of(
                                filePathValidators,
                                Map.of(
                                        DIRECTORY_PARAM_NAME, FilePathType.REQUIRED_DIRECTORY,
                                        OUTPUT_FILE_PARAM_NAME, FilePathType.OPTIONAL_FILE
                                )
                        )
                )
                .start(readLoanCountMasterStep)
                .next(syncLoanCountStep)
                .build();
    }
}
