package com.bookspot.batch.job.loan;

import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.temp_FilePathJobParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class LoanSyncJobConfig {
    public static final String AGGREGATED_FILE_PARAM_NAME = "aggregatedFilePath";
    public static final String AGGREGATED_FILE_PATH = "#{jobParameters['aggregatedFilePath']}";

    private final JobRepository jobRepository;
    private final CustomFilePathValidators filePathValidators;

    @Bean
    public Job loanSyncJob(Step syncLoanCountStep) {
        return new JobBuilder("loanAggregatedJob", jobRepository)
                .start(syncLoanCountStep) //- 저장한 파일을 DB에 반영 - 새로 나온 책 + 최근 대출 횟수 반영
                .validator(
                        temp_FilePathJobParameterValidator.REQUIRED_FILE(
                                filePathValidators,
                                AGGREGATED_FILE_PARAM_NAME
                        )
                )
                .build();
    }
}
