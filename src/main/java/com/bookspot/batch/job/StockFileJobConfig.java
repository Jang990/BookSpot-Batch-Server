package com.bookspot.batch.job;

import com.bookspot.batch.job.extractor.CommonStringJobParamExtractor;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
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

@Configuration
@RequiredArgsConstructor
public class StockFileJobConfig {
    private final JobRepository jobRepository;
    private final CustomFilePathValidators filePathValidators;

    public static final String DOWNLOAD_DIR_PARAM_NAME = "downloadDir";
    public static final String DOWNLOAD_DIR_PARAM = "#{jobParameters['downloadDir']}";

    @Bean
    public Step stockFileJobStep(Job stockFileJob, JobLauncher jobLauncher) {
        return new StepBuilder("stockFileJobStep", jobRepository)
                .job(stockFileJob)
                .launcher(jobLauncher)
                .parametersExtractor(stockFileJobParamExtractor())
                .build();
    }


    @Bean
    public JobParametersExtractor stockFileJobParamExtractor() {
        return new CommonStringJobParamExtractor(
                BookSpotParentJobConfig.DOWNLOAD_DIR_PARAM_NAME,
                DOWNLOAD_DIR_PARAM_NAME
        );
    }

    @Bean
    public Job stockFileJob(Step stockCsvDownloadStep) {
        // naru_detail이 있는 도서관 파일 다운로드
        return new JobBuilder("stockFileJob", jobRepository)
                .start(stockCsvDownloadStep)
                .validator(
                        FilePathJobParameterValidator.REQUIRED_DIRECTORY(
                                filePathValidators,
                                DOWNLOAD_DIR_PARAM_NAME
                        )
                )
                .build();
    }
}
