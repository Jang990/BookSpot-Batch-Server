package com.bookspot.batch.job;

import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import com.bookspot.batch.job.validator.temp_FilePathJobParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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

    @Bean
    public Job stockNormalizeJob(
            Step isbnIdMapInitStep,
            Step stockNormalizeMasterStep,
            Step isbnIdMapCleaningStep) {
        return new JobBuilder("stockNormalizeJob", jobRepository)
                .start(isbnIdMapInitStep)
                .next(stockNormalizeMasterStep)
                .next(isbnIdMapCleaningStep)
                .validator(
                        temp_FilePathJobParameterValidator.of(
                                filePathValidators,
                                Map.of(
                                        SOURCE_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY,

                                        NORMALIZE_DIR_PARAM_NAME,
                                        FilePathType.REQUIRED_DIRECTORY
                                )
                        )
                )
                .build();
    }
}
